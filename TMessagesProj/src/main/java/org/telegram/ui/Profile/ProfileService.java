package org.telegram.ui.Profile;

import static org.telegram.messenger.LocaleController.formatString;

import android.os.Bundle;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.tgnet.TLRPC;

public class ProfileService {
    public void handleInviteToGroup(BaseFragment baseFragment, long userId, int currentAccount, ProfileActivity profileActivity, Theme.ResourcesProvider resourcesProvider, Runnable onSuccess) {
        final TLRPC.User user = baseFragment.getMessagesController().getUser(userId);
        if (user == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putBoolean("onlySelect", true);
        args.putInt("dialogsType", DialogsActivity.DIALOGS_TYPE_ADD_USERS_TO);
        args.putBoolean("resetDelegate", false);
        args.putBoolean("closeFragment", false);
//                    args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupAlertText", R.string.AddToTheGroupAlertText, UserObject.getUserName(user), "%1$s"));
        DialogsActivity fragment = new DialogsActivity(args);
        fragment.setDelegate((fragment1, dids, message, param, notify, scheduleDate, topicsFragment) -> {
            long did = dids.get(0).dialogId;

            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-did);
            if (chat != null && (chat.creator || chat.admin_rights != null && chat.admin_rights.add_admins)) {
                baseFragment.getMessagesController().checkIsInChat(false, chat, user, (isInChatAlready, rightsAdmin, currentRank) -> AndroidUtilities.runOnUIThread(() -> {
                    ChatRightsEditActivity editRightsActivity = new ChatRightsEditActivity(userId, -did, rightsAdmin, null, null, currentRank, ChatRightsEditActivity.TYPE_ADD_BOT, true, !isInChatAlready, null);
                    editRightsActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                        @Override
                        public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
//                            disableProfileAnimation = true;
                            onSuccess.run();
                            fragment.removeSelfFromStack();
                            baseFragment.getNotificationCenter().removeObserver(profileActivity, NotificationCenter.closeChats);
                            baseFragment.getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                        }

                        @Override
                        public void didChangeOwner(TLRPC.User user) {
                        }
                    });
                    baseFragment.presentFragment(editRightsActivity);
                }));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity(), resourcesProvider);
                builder.setTitle(LocaleController.getString(R.string.AddBot));
                String chatName = chat == null ? "" : chat.title;
                builder.setMessage(AndroidUtilities.replaceTags(formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, UserObject.getUserName(user), chatName)));
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                builder.setPositiveButton(LocaleController.getString(R.string.AddBot), (di, i) -> {
//                    disableProfileAnimation = true;
                    onSuccess.run();
                    Bundle args1 = new Bundle();
                    args1.putBoolean("scrollToTopOnResume", true);
                    args1.putLong("chat_id", -did);
                    if (!baseFragment.getMessagesController().checkCanOpenChat(args1, fragment1)) {
                        return;
                    }
                    ChatActivity chatActivity = new ChatActivity(args1);

                    baseFragment.getNotificationCenter().removeObserver(profileActivity, NotificationCenter.closeChats);
                    baseFragment.getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                    baseFragment.getMessagesController().addUserToChat(-did, user, 0, null, chatActivity, true, null, null);
                    baseFragment.presentFragment(chatActivity, true);
                });
                baseFragment.showDialog(builder.create());
            }
            return true;
        });
        baseFragment.presentFragment(fragment);
    }
}
