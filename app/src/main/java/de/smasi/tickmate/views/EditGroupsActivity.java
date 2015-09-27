package de.smasi.tickmate.views;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.smasi.tickmate.R;
import de.smasi.tickmate.database.TracksDataSource;
import de.smasi.tickmate.models.Group;

public class EditGroupsActivity extends ListActivity {

	ArrayAdapter<Group> groupsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_groups);

        // Show the Up button in the action bar.
        setupActionBar();

		loadGroups();
        registerForContextMenu(this.getListView());
    }

    protected void loadGroups() {

        Group[] groupArray = new Group[0];
        groupArray = TracksDataSource.getInstance().getGroups().toArray(groupArray);
        groupsAdapter = new GroupListAdapter(this, groupArray);
        this.getListView().setAdapter(groupsAdapter);
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Copied from EditTracksActivity:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_add_group:
            case R.id.action_add_group_menu:
                Intent intent = new Intent(this, ChooseGroupActivity.class);
                startActivityForResult(intent, 1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        editGroup(groupsAdapter.getItem(position));
    }

    private void editGroup(Group g) {
        Intent intent = new Intent(this, GroupPreferenceActivity.class);
        intent.putExtra("group_id", g.getId());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Nothing to do here except refresh the screen
        loadGroups();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            case R.id.edit_groups_edit: {
                Group g = groupsAdapter.getItem((int) info.id);
                editGroup(g);
                return true;
            }

            // Consider whether we may want to let the user re-order the groups, in which case ...
            // ... model the code in EditTracksActivity, after "case R.id.edit_tracks_moveup"
            // Consider also whether the user will be able to enable/disable groups.

            case R.id.edit_groups_delete: {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.alert_delete_group_title)
                        .setMessage(R.string.alert_delete_group_message)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Group g = groupsAdapter.getItem((int) info.id);

                                TracksDataSource.getInstance().deleteGroup(g);
                                loadGroups();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_groups_context_menu, menu);
    }
}