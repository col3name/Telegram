package org.telegram.ui;

import static java.security.AccessController.getContext;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.collection.LongSparseArray;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;

import java.util.ArrayList;

public class QuickShare {
    private ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();
    private LongSparseArray<TLRPC.Dialog> dialogsMap = new LongSparseArray<>();
    private final int MAX_USERS = 5;
    private int currentAccount;
    private ChatActivity parentFragment;
    private GridLayout gridView;

    public QuickShare(int currentAccount, ChatActivity parentFragment) {
        this.currentAccount = currentAccount;
        this.parentFragment = parentFragment;
        fetchDialogs();
    }

    public ArrayList<TLRPC.Dialog> getDialogs() {
        return dialogs;
    }

    public void show(ChatMessageCell cell, TLRPC.User user, float touchX, float touchY) {
        if (gridView == null) {
            return;
        }
        int[] position = new int[2];
        cell.getLocationOnScreen(position);
        int x = position[0];
        int height = cell.getMeasuredHeight();
        int y = position[1] + height - 420;
        gridView.setTranslationY(y);
        gridView.setVisibility(View.VISIBLE);
    }

    public void onLongPress() {

    }


    private boolean isTouchInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();

        return (x >= left && x <= right && y >= top && y <= bottom);
    }

    public void fetchDialogs() {
        dialogs.clear();
        dialogsMap.clear();

        long selfUserId = UserConfig.getInstance(currentAccount).clientUserId;
        if (!MessagesController.getInstance(currentAccount).dialogsForward.isEmpty()) {
            TLRPC.Dialog dialog = MessagesController.getInstance(currentAccount).dialogsForward.get(0);
            dialogs.add(dialog);
            dialogsMap.put(dialog.id, dialog);
        }
        ArrayList<TLRPC.Dialog> archivedDialogs = new ArrayList<>();
        ArrayList<TLRPC.Dialog> allDialogs = MessagesController.getInstance(currentAccount).getAllDialogs();

        for (int a = 0; a < allDialogs.size(); a++) {
            TLRPC.Dialog dialog = allDialogs.get(a);
            if (!(dialog instanceof TLRPC.TL_dialog)) {
                continue;
            }
            if (dialog.id == selfUserId) {
                continue;
            }
            if (!DialogObject.isEncryptedDialog(dialog.id)) {
                if (DialogObject.isUserDialog(dialog.id)) {
                    if (dialog.folder_id == 1) {
                        archivedDialogs.add(dialog);
                    } else {
                        dialogs.add(dialog);
                    }
                    dialogsMap.put(dialog.id, dialog);
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-dialog.id);
                    if (!(chat == null || ChatObject.isNotInChat(chat) || chat.gigagroup && !ChatObject.hasAdminRights(chat) || ChatObject.isChannel(chat) && !chat.creator && (chat.admin_rights == null || !chat.admin_rights.post_messages) && !chat.megagroup)) {
                        if (dialog.folder_id == 1) {
                            archivedDialogs.add(dialog);
                        } else {
                            dialogs.add(dialog);
                        }
                        dialogsMap.put(dialog.id, dialog);
                    }
                }
            }
            if (dialogs.size() >= MAX_USERS) {
                break;
            }
        }

        if (dialogs.size() < MAX_USERS) {
            for (int i = 0; i < MAX_USERS - dialogs.size() && i < archivedDialogs.size(); i++) {
                dialogs.add(archivedDialogs.get(i));
            }
        }
        if (gridView == null) {
            gridView = new GridLayout(parentFragment.getContext());
        }
        gridView.setBackgroundColor(Color.WHITE);
        gridView.removeAllViews();
        gridView.setOrientation(GridLayout.HORIZONTAL);
        // gridView.setBackgroundColor(Color.RED);
        // gridView.setNumColumns(5);
        gridView.setColumnCount(5);
        // gridView.setVerticalSpacing(10);

        gridView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        gridView.setHorizontalScrollBarEnabled(true);
        int i = 0;
        for (TLRPC.Dialog dialog : dialogs) {
//            LinearLayout forwardItemLayout = new LinearLayout(parentFragment.getContext());
//            forwardItemLayout.setOrientation(LinearLayout.VERTICAL);

            TLRPC.User user = parentFragment.getMessagesController().getInstance(currentAccount).getUser(dialog.id);

            BackupImageView avatar = new BackupImageView(parentFragment.getContext());
            avatar.setClipBounds(new Rect());
            AvatarDrawable avatarDrawable = new AvatarDrawable() {
                @Override
                public void invalidateSelf() {
                    super.invalidateSelf();
                    avatar.invalidate();
                }
            };

            if (i == 0) {
                avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_SAVED);
                avatarDrawable.setScaleSize(0.6f);
                avatar.setImage(null, null, avatarDrawable, user);
            } else if (DialogObject.isUserDialog(dialog.id)) {
                avatar.setForUserOrChat(user, avatarDrawable);
            } else {
                TLRPC.Chat chat = parentFragment.getMessagesController().getInstance(currentAccount).getChat(-dialog.id);
                avatarDrawable.setInfo(currentAccount, chat);
                avatar.setForUserOrChat(chat, avatarDrawable);
            }
            avatar.setRoundRadius(AndroidUtilities.dp(24));

//            forwardItemLayout.addView(avatar);
            i++;

            gridView.addView(avatar);
        }
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                parentFragment.contentView.requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Handle touch down event

//                        myButton.setBackgroundColor(Color.DKGRAY); // Change color to dark gray on press
                        Toast.makeText(parentFragment.getParentActivity(), "Button Pressed Down", Toast.LENGTH_SHORT).show();
                        return true;

                    case MotionEvent.ACTION_UP:
                        Toast.makeText(parentFragment.getParentActivity(), "Button Pressed Down", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        gridView.setVisibility(View.GONE);
        parentFragment.contentView.addView(gridView);
    }

    public void hide() {
        gridView.setVisibility(View.GONE);
    }
}
