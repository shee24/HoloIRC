package com.fusionx.lightirc.fragments.serversetttings;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.fusionx.lightirc.R;
import com.fusionx.lightirc.adapters.SelectionAdapter;
import com.fusionx.lightirc.interfaces.ServerSettingsCallbacks;
import com.fusionx.lightirc.promptdialogs.ChannelNamePromptDialogBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static com.fusionx.lightirc.misc.PreferenceKeys.AutoJoin;

public class ListViewSettingsFragment extends ListFragment implements AbsListView
        .MultiChoiceModeListener, android.view.ActionMode.Callback,
        AdapterView.OnItemClickListener {
    private SelectionAdapter<String> adapter;
    private boolean modeStarted = false;
    private ServerSettingsCallbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallbacks = (ServerSettingsCallbacks) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setLayoutTransition(new LayoutTransition());
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflate = mode.getMenuInflater();
        inflate.inflate(R.menu.activty_server_settings_cab, menu);

        modeStarted = true;

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.getItem(0).setVisible(!(getListView().getCheckedItemCount() > 1));
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        final ArrayList<String> positions = adapter.getSelectedItems();

        switch (item.getItemId()) {
            case R.id.activity_server_settings_cab_edit:
                final String edited = adapter.getItem(0);
                final ChannelNamePromptDialogBuilder dialog = new ChannelNamePromptDialogBuilder
                        (getActivity(), edited) {
                    @Override
                    public void onOkClicked(final String input) {
                        adapter.remove(edited);
                        adapter.add(input);
                    }
                };
                dialog.show();

                mode.finish();
                return true;
            case R.id.activity_server_settings_cab_delete:
                for (String selected : positions) {
                    adapter.remove(selected);
                }
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position,
                                          long id, boolean checked) {
        mode.invalidate();

        if (checked) {
            adapter.addSelection(position);
        } else {
            adapter.removeSelection(position);
        }

        int selectedItemCount = getListView().getCheckedItemCount();

        if (selectedItemCount != 0) {
            final String quantityString = getResources()
                    .getQuantityString(R.plurals.channel_selection,
                            selectedItemCount, selectedItemCount);
            mode.setTitle(quantityString);
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode arg0) {
        adapter.clearSelection();

        modeStarted = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setMultiChoiceModeListener(this);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflate) {
        inflate.inflate(R.menu.activity_server_settings_channellist_ab, menu);
        super.onCreateOptionsMenu(menu, inflate);
    }

    @Override
    public View onCreateView(final LayoutInflater inflate, final ViewGroup container,
                             final Bundle savedInstanceState) {
        adapter = new SelectionAdapter<>(getActivity(), new TreeSet<String>());

        final SharedPreferences settings = getActivity()
                .getSharedPreferences(mCallbacks.getFileName(), Context.MODE_PRIVATE);
        final Set<String> set = settings.getStringSet(AutoJoin, new HashSet<String>());
        for (final String channel : set) {
            adapter.add(channel);
        }

        setListAdapter(adapter);
        setHasOptionsMenu(true);

        return super.onCreateView(inflate, container, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.activity_server_settings_ab_add:
                final ChannelNamePromptDialogBuilder dialog =
                        new ChannelNamePromptDialogBuilder(getActivity()) {
                            @Override
                            public void onOkClicked(final String input) {
                                adapter.add(input);
                            }
                        };
                dialog.show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPause() {
        final SharedPreferences settings = getActivity().getSharedPreferences(mCallbacks
                .getFileName(), Context.MODE_PRIVATE);
        settings.edit().putStringSet(AutoJoin, adapter.getCopyOfItems()).commit();

        super.onPause();
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int i,
                            final long l) {
        if (!modeStarted) {
            getActivity().startActionMode(this);
        }

        final boolean checked = adapter.getSelectedItems().contains(adapter.getItem(i));
        getListView().setItemChecked(i, !checked);
    }
}