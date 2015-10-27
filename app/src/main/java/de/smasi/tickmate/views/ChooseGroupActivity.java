package de.smasi.tickmate.views;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.smasi.tickmate.database.DataSource;
import de.smasi.tickmate.models.Group;
//import de.smasi.tickmate.models.Track;

public class ChooseGroupActivity extends ListActivity {

    ArrayAdapter<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int resourceId = getResources().getIdentifier("groups", "xml", getPackageName());
        XmlResourceParser xrp = getResources().getXml(resourceId);
        Group[] groupList = importGroups(xrp);
        groups = new GroupListAdapter(this, groupList);
        this.getListView().setAdapter(groups);
    }

     /**
     * Return an array of Groups derived by parsing an xml file,  which is accessed via the given
     *   XmlPullParser object
     *
     * @param xmlParser The parser for the xml file,  which is to be parsed into an array of Groups.
     *
     * @return An array of groups derived by parsing the xml file.
     *
     */
    private Group[] importGroups(XmlPullParser xmlParser) {
        int eventType;
        List<Group> listOfGroups = new LinkedList<>();

        try {
            eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.v("XML", "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
//                    Log.v("XML", "Start tag " + xmlParser.getName());
                    if (xmlParser.getName().equals("group")) {
                        String name = xmlParser.getAttributeValue(null, "name");
                        String description = xmlParser.getAttributeValue(null, "description");
                        if (name == null || description == null) {
                            Log.w("Tickmate", "groups.xml. Ignoring entry.");
                        }
                        Group g = new Group(name, description);
                        // Ignore the icon, for now
//                        g.setIcon(getResources().getResourceEntryName(xmlParser.getAttributeResourceValue(null, "icon", R.drawable.glyphicons_000_glass_white)));
                        listOfGroups.add(g);
                    } else if (xmlParser.getName().equals("section")) {
                        Group g = new Group(xmlParser.getAttributeValue(null, "name"));
                        g.setSectionHeader(true);
                        listOfGroups.add(g);
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
//                    Log.v("XML", "End tag " + xmlParser.getName());
                } else if (eventType == XmlPullParser.TEXT) {
//                    Log.v("XML", "Text " + xmlParser.getText());
                }
                eventType = xmlParser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e("XML", "XmlPullParserException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("XML", "IOException: " + e.getMessage());
        }
//        Log.v("XML", listOfGroups.size() + " groups loaded");

        Group[] groupList = new Group[listOfGroups.size()];
        listOfGroups.toArray(groupList);
        return groupList;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Group g = (Group) getListView().getAdapter().getItem(position);

        if (!g.isSectionHeader()) {
            DataSource.getInstance().storeGroup(g);
            Intent data = new Intent();
            data.putExtra("insert_id", g.getId());
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
