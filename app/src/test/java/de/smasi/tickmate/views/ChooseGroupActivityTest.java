package de.smasi.tickmate.views;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.smasi.tickmate.BuildConfig;
import de.smasi.tickmate.TickmateTestRunner;
import de.smasi.tickmate.models.Group;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Config(sdk = 17, constants = BuildConfig.class)
@RunWith(TickmateTestRunner.class)
public class ChooseGroupActivityTest {

    private ActivityController<ChooseGroupActivity> mActivityController;
    private ChooseGroupActivity mActivity;

    @Before
    public void setUp() {
        mActivityController = Robolectric.buildActivity(ChooseGroupActivity.class);
        mActivity = mActivityController.create().start().get();
    }

    @After
    public void tearDown() {
        mActivityController = mActivityController.pause().stop().destroy();
        mActivity = null;
    }

    @Test @Ignore
    public void test_onCreate() {
        // todo
//        DataSource.getInstance().getTracks();
    }


    @Test
    public void test_importGroups() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, XmlPullParserException, FileNotFoundException {
        Method importGroupsMethod;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xppValidInput = factory.newPullParser();
        File validFile= new File("app/src/test/res/xml/groups_test_valid.xml");
        xppValidInput.setInput(new FileInputStream(validFile), "utf8");

        Class[] args = new Class[1];
        args[0] = XmlPullParser.class;
        importGroupsMethod = mActivity.getClass().getDeclaredMethod("importGroups", args);
        importGroupsMethod.setAccessible(true);

        Group[] groupList = (Group[]) importGroupsMethod.invoke(mActivity, xppValidInput);

        assertNotNull(groupList);
        assertEquals(5, groupList.length);
        assertEquals("Section 1", groupList[0].getName());
        assertEquals("Group 3 description", groupList[4].getDescription());
    }


    @Test
    public void test_importGroups_invalidInput() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, XmlPullParserException, FileNotFoundException {
        Method importGroupsMethod;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xppInvalidInput = factory.newPullParser();
        File invalidFile = new File("app/src/test/res/xml/groups_test_invalid.xml");
        xppInvalidInput.setInput(new FileInputStream(invalidFile), "utf8");

        Class[] args = new Class[1];
        args[0] = XmlPullParser.class;
        importGroupsMethod = mActivity.getClass().getDeclaredMethod("importGroups", args);
        importGroupsMethod.setAccessible(true);

        Group[] groupList = (Group[]) importGroupsMethod.invoke(mActivity, xppInvalidInput);

        assertNotNull(groupList);
        assertEquals(4, groupList.length); // Missing one due to malformed xml
        assertNotNull(groupList[1].getName());  // Missing name isn't set to null
        assertEquals("Group 3 description", groupList[3].getDescription());
    }


    @Test
    public void test_onListItemClick() {
        // create a Group that is not a section header
        Group g = new Group("groupName");
        g.setSectionHeader(false);
        g.setId(0);

        // set adapter
        GroupListAdapter groupListAdapter = new GroupListAdapter(mActivity, new Group[] { g });
        mActivity.getListView().setAdapter(groupListAdapter);

        // click on Group
        mActivity.onListItemClick(mActivity.getListView(), null, 0, 0l);

        // verify that activity is finishing
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void test_onListItemClick_header() {
        // create a Group that is a section header
        Group g = new Group("groupName");
        g.setSectionHeader(true);
        g.setId(0);

        // set adapter
        GroupListAdapter groupListAdapter = new GroupListAdapter(mActivity, new Group[] { g });
        mActivity.getListView().setAdapter(groupListAdapter);

        // click on Group
        mActivity.onListItemClick(mActivity.getListView(), null, 0, 0l);

        // verify that activity is not finishing
        assertFalse(mActivity.isFinishing());
    }
}
