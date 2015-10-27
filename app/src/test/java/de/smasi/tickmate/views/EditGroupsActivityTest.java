package de.smasi.tickmate.views;

import android.content.ComponentName;
import android.content.Intent;
import android.view.MenuItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import java.lang.reflect.Field;

import de.smasi.tickmate.BuildConfig;
import de.smasi.tickmate.R;
import de.smasi.tickmate.TickmateTestRunner;
import de.smasi.tickmate.database.DataSource;
import de.smasi.tickmate.database.DatabaseOpenHelper;
import de.smasi.tickmate.models.Group;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 17, constants = BuildConfig.class)
@RunWith(TickmateTestRunner.class)
public class EditGroupsActivityTest {

    private ActivityController<EditGroupsActivity> mActivityController;
    private EditGroupsActivity mActivity;

    @Before
    public void setUp() {
        Group g = new Group("groupName");
        g.setId(1);

        DataSource dataSource = DataSource.getInstance();
        dataSource.storeGroup(g);

        mActivityController = Robolectric.buildActivity(EditGroupsActivity.class);
        mActivity = mActivityController.create().start().get();
    }

    @After
    public void tearDown() {
        mActivityController = mActivityController.pause().stop().destroy();
        mActivity = null;

        Field databseOpenHelperInstance, tracksDataSourceInstance;

        try {
            databseOpenHelperInstance = DatabaseOpenHelper.class.getDeclaredField("sharedInstance");
            databseOpenHelperInstance.setAccessible(true);
            databseOpenHelperInstance.set(null, null);

            tracksDataSourceInstance = DataSource.class.getDeclaredField("mInstance");
            tracksDataSourceInstance.setAccessible(true);
            tracksDataSourceInstance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Test @Ignore
    public void test_onCreate_loadGroups() {
        assertNotNull(mActivity.getListAdapter());
        assertEquals(mActivity.getListAdapter().getCount(), 1);
    }

    @Test
    public void test_onOptionsItemSelected_home() {
        MenuItem addGroupMenuItem = new RoboMenuItem(android.R.id.home);

        mActivity.onOptionsItemSelected(addGroupMenuItem);
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void test_onOptionsItemSelected_addGroup() {
        ShadowActivity shadowActivity = Shadows.shadowOf(mActivity);

        MenuItem addGroupMenuItem = new RoboMenuItem(R.id.action_add_group);

        mActivity.onOptionsItemSelected(addGroupMenuItem);

        Intent intent = shadowActivity.peekNextStartedActivityForResult().intent;
        assertEquals(new ComponentName(mActivity, ChooseGroupActivity.class), intent.getComponent());
    }

    @Test
    public void test_onOptionsItemSelected_addGroupMenu() {
        ShadowActivity shadowActivity = Shadows.shadowOf(mActivity);

        MenuItem addGroupMenuItem = new RoboMenuItem(R.id.action_add_group_menu);

        mActivity.onOptionsItemSelected(addGroupMenuItem);

        Intent intent = shadowActivity.peekNextStartedActivityForResult().intent;
        assertEquals(new ComponentName(mActivity, ChooseGroupActivity.class), intent.getComponent());
    }

    @Test @Ignore
    public void test_onListItemClick() {
        mActivity.onListItemClick(null, null, 0, 0l);
    }

    @Test
    public void test_onContextItemSelected() {
    }
}
