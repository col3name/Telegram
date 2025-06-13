package org.telegram.ui.Profile;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ChannelAdminLogActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;

import java.util.function.Function;

public class BanFromGroupView extends FrameLayout {
    public BanFromGroupView(@NonNull Context context) {
        super(context);
    }
//
//    private long userId;
//    private long chatId;
//    private final boolean banFromGroup;
//    private TLRPC.Chat chat;
//    private TLRPC.ChannelParticipant currentChannelParticipant;
//
//    private ProfileActivity profileActivity;
//
//    public BanFromGroupView(Context context, long userId, long chatId, ProfileActivity profileActivity, boolean banFromGroup) {
//        super(context);
//        this.userId = userId;
//
//        this.chatId = chatId;
//        this.profileActivity = profileActivity;
//        this.banFromGroup = banFromGroup;
//        this.chat = MessagesController.getInstance().getChat(chatId);
//
//        setWillNotDraw(false);
//        setupView(context);
//    }
//
//    private void setupView(Context context) {
//        // Draw background
//        setWillNotDraw(false);
//
//
//       setWillNotDraw(false);
//        setOnClickListener(v -> {
//            openChatRightsEdit();
//        });
//
//        TextView textView = new TextView(context);
//        textView.setTextColor(profileActivity.getThemedColor(Theme.key_text_RedRegular));
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTypeface(AndroidUtilities.bold());
//        textView.setText(LocaleController.getString(R.string.BanFromTheGroup));
//        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 1, 0, 0));
//
////        return banGroupFrameLayout;
//    }
//
//
//
//    private void openChatRightsEdit() {
//        ChatRightsEditActivity fragment = new ChatRightsEditActivity(userId, banFromGroup, null, chat.default_banned_rights, currentChannelParticipant != null ? currentChannelParticipant.banned_rights : null, "", ChatRightsEditActivity.TYPE_BANNED, true, false, null);
//        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
//            @Override
//            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
//                profileActivity.removeSelfFromStack();
//                TLRPC.User user = profileActivity.getMessagesController().getUser(userId);
//                if (user != null && chat != null && userId != 0 && fragment != null && fragment.banning && fragment.getParentLayout() != null) {
//                    for (BaseFragment fragment : fragment.getParentLayout().getFragmentStack()) {
//                        if (fragment instanceof ChannelAdminLogActivity) {
//                            ((ChannelAdminLogActivity) fragment).reloadLastMessages();
//                            AndroidUtilities.runOnUIThread(() -> {
//                                BulletinFactory.createRemoveFromChatBulletin(fragment, user, chat.title).show();
//                            });
//                            return;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void didChangeOwner(TLRPC.User user) {
//                profileActivity.getUndoView().showWithAction(-chatId, profileActivity.getCurrentChat().megagroup ? UndoView.ACTION_OWNER_TRANSFERED_GROUP : UndoView.ACTION_OWNER_TRANSFERED_CHANNEL, user);
//            }
//        });
//        profileActivity.presentFragment(fragment);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
        Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
        Theme.chat_composeShadowDrawable.draw(canvas);
        canvas.drawRect(0, bottom, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
    }
}
