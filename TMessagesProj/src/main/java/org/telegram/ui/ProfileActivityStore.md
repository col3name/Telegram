## TODO
#### actions :
- stop bot button (bot) || AlertsCreator.showBlockReportSpamAlert(ProfileActivity.this, userId
- video and audio call button
- mute/unmute button 
- "discuss" text instead "message" button (channel)
- gift button (channel)
- life stream (channel)
- add story (channel)
- voice chat (group)
- add story (admin channel). Need implement flow of enabling stories with boost


    private void setAvatarExpandProgress(float animatedFracture) {

- premium background
- render gifts on profile if have them


# tasks
- bot buttons
- channel buttons (need check buttons view after leave and join, when join into group and leave from group, when changed admin rights, when linked/unlinked discuss group from channel
- [ ] profile buttons
- [ ] business
- [ ] profile gift new position and new animation
- [ ] group (need check button view after leave and join)
- [ ] group forum 
- [ ] group topic

- [ ] show mute popup by click new mute button

- [ ] animate avatar after expand 
- [ ] animate avatar after collapse (use signed distance field for circle at top)
- [ ] animate collapse and expand button rows
- [ ] implement animation of adding and removing button from buttonRow when joined, leaved, changed admin rights, when linked/unlinked discuss group from channel

- [x] leave button from group for not admin

### channel buttons:
 - join, mute, share, report (not joined into channel)
 - mute, share, leave
 - mute, discuss, share, leave (linked discuss chat)
 - mute, gift, share, leave (not linked discuss chat)
 - live stream, mute, addStory (admin)


###
 - pu

    private void createActionBarMenu(boolean animated) {


 actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
fileDrawable.addSecondParentView(avatarImage);

 headerCell.setText(LocaleController.getString(R.string.Info) + "info");

 headerCell.setHeight(0);
                    headerCell.setText("");

 // voice chat
            callItem = menu.addItem(call_item, R.drawable.msg_voicechat2);

LocaleController.formatUserStatus(currentAccount, user, isOnline, shortStatus ? new boolean[1] : null) + "sf";

     return actionBar;


        // Add horizontal row of buttons at the top
        LinearLayout buttonRow = new LinearLayout(context);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setGravity(Gravity.CENTER);
        buttonRow.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));

        // Message button
        Button messageButton = new Button(context);
        messageButton.setText("Message");
        messageButton.setOnClickListener(v -> {
            // TODO: Implement message action
        });
        buttonRow.addView(messageButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Mute/Unmute button
        Button muteButton = new Button(context);
        muteButton.setText("Mute/Unmute");
        muteButton.setOnClickListener(v -> {
            // TODO: Implement mute/unmute action
        });
        buttonRow.addView(muteButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Call button
        Button callButton = new Button(context);
        callButton.setText("Call");
        callButton.setOnClickListener(v -> {
            // TODO: Implement call action
        });
        buttonRow.addView(callButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        // Video Call button
        Button videoCallButton = new Button(context);
        videoCallButton.setText("Video Call");
        videoCallButton.setOnClickListener(v -> {
            // TODO: Implement video call action
        });
        buttonRow.addView(videoCallButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

//        frameLayout.addView(buttonRow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.FILL_HORIZONTAL));


 private void handleCustomClick(View view, int position, float x, float y) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
        long did;
        if (dialogId != 0) {
            did = dialogId;
        } else if (userId != 0) {
            did = userId;
        } else {
            did = -chatId;
        }
        if (LocaleController.isRTL && x <= AndroidUtilities.dp(76) || !LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76)) {
            NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
            boolean checked = !checkCell.isChecked();

            boolean defaultEnabled = getNotificationsController().isGlobalNotificationsEnabled(did, false, false);

            String key = NotificationsController.getSharedPrefKey(did, topicId);
            if (checked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (defaultEnabled) {
                    editor.remove("notify2_" + key);
                } else {
                    editor.putInt("notify2_" + key, 0);
                }
                if (topicId == 0) {
                    getMessagesStorage().setDialogFlags(did, 0);
                    TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
                    if (dialog != null) {
                        dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                    }
                }
                editor.apply();
            } else {
                int untilTime = Integer.MAX_VALUE;
                SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
                SharedPreferences.Editor editor = preferences.edit();
                long flags;
                if (!defaultEnabled) {
                    editor.remove("notify2_" + key);
                    flags = 0;
                } else {
                    editor.putInt("notify2_" + key, 2);
                    flags = 1;
                }
                getNotificationsController().removeNotificationsForDialog(did);
                if (topicId == 0) {
                    getMessagesStorage().setDialogFlags(did, flags);
                    TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
                    if (dialog != null) {
                        dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                        if (defaultEnabled) {
                            dialog.notify_settings.mute_until = untilTime;
                        }
                    }
                }
                editor.apply();
            }
            updateExceptions();
            getNotificationsController().updateServerNotificationsSettings(did, topicId);
            checkCell.setChecked(checked);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForPosition(notificationsRow);
            if (holder != null) {
                // listAdapter.onBindViewHolder(holder, notificationsRow);
            }
            return;
        }
        ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, currentAccount, null, true, true, new ChatNotificationsPopupWrapper.Callback() {
            @Override
            public void toggleSound() {
                SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
                boolean enabled = !preferences.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(did, topicId), true);
                preferences.edit().putBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(did, topicId), enabled).apply();
                if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                    BulletinFactory.createSoundEnabledBulletin(ProfileActivity.this, enabled ? NotificationsController.SETTING_SOUND_ON : NotificationsController.SETTING_SOUND_OFF, getResourceProvider()).show();
                }
            }

            @Override
            public void muteFor(int timeInSeconds) {
                if (timeInSeconds == 0) {
                    if (getMessagesController().isDialogMuted(did, topicId)) {
                        toggleMute();
                    }
                    if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                        BulletinFactory.createMuteBulletin(ProfileActivity.this, NotificationsController.SETTING_MUTE_UNMUTE, timeInSeconds, getResourceProvider()).show();
                    }
                } else {
                    getNotificationsController().muteUntil(did, topicId, timeInSeconds);
                    if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                        BulletinFactory.createMuteBulletin(ProfileActivity.this, NotificationsController.SETTING_MUTE_CUSTOM, timeInSeconds, getResourceProvider()).show();
                    }
                    updateExceptions();
                    if (notificationsRow >= 0 && listAdapter != null) {
                        listAdapter.notifyItemChanged(notificationsRow);
                    }
                }
            }

            @Override
            public void showCustomize() {
                if (did != 0) {
                    Bundle args = new Bundle();
                    args.putLong("dialog_id", did);
                    args.putLong("topic_id", topicId);
                    presentFragment(new ProfileNotificationsActivity(args, resourcesProvider));
                }
            }

            @Override
            public void toggleMute() {
                boolean muted = getMessagesController().isDialogMuted(did, topicId);
                getNotificationsController().muteDialog(did, topicId, !muted);
                if (ProfileActivity.this.fragmentView != null) {
                    BulletinFactory.createMuteBulletin(ProfileActivity.this, !muted, null).show();
                }
                updateExceptions();
                if (notificationsRow >= 0 && listAdapter != null) {
                    listAdapter.notifyItemChanged(notificationsRow);
                }
            }

            @Override
            public void openExceptions() {
                Bundle bundle = new Bundle();
                bundle.putLong("dialog_id", did);
                TopicsNotifySettingsFragments notifySettings = new TopicsNotifySettingsFragments(bundle);
                notifySettings.setExceptions(notificationsExceptionTopics);
                presentFragment(notifySettings);
            }
        }, getResourceProvider());
        chatNotificationsPopupWrapper.update(did, topicId, notificationsExceptionTopics);
        if (AndroidUtilities.isTablet()) {
            View v = parentLayout.getView();
            x += v.getX() + v.getPaddingLeft();
            y += v.getY() + v.getPaddingTop();
        }
        chatNotificationsPopupWrapper.showAsOptions(ProfileActivity.this, view, x, y);
    }
