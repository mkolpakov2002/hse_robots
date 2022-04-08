package ru.hse.control_system_v2.dbprotocol;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ru.hse.control_system_v2.R;

public class ProtocolRepo extends HashMap<String, Byte> {

    private static final String LOG_TAG = "XMLParcer";
    private ArrayList<String> queryTags;
    private HashMap<String, Byte> moveCodes;
    private Context context;
    ProtocolDBHelper dbHelper;

    public static final List<String> mainLabels = new ArrayList<>(Arrays.asList("class_android", "class_computer", "class_arduino", "type_sphere", "type_anthropomorphic",
            "type_cubbi", "type_computer", "no_class", "no_type", "redo_command", "new_command", "type_move", "type_tele",
            "STOP", "FORWARD", "FORWARD_STOP", "BACK", "BACK_STOP", "LEFT", "LEFT_STOP", "RIGHT", "RIGHT_STOP"));

    public static final HashMap<String, Byte> possibilitiesSettings = new HashMap<>();

    private final HashMap<String, Byte> newProtoCommands = new HashMap<>();

    public ProtocolRepo(Context context, String name) {
        this.context = context;
        dbHelper = ProtocolDBHelper.getInstance(context);
        Log.d("mLog", "name = " + name);
        queryTags = new ArrayList<>();
        moveCodes = new HashMap<>();
        if (!name.isEmpty()){
            possibilitiesSettings.put("camera", (byte) 0x0);
            possibilitiesSettings.put("move", (byte) 0x0);
            possibilitiesSettings.put("package_data", (byte) 0x0);
            for(String key: mainLabels){
                moveCodes.put(key, (byte) 0x0);
            }
            parseCodes(name);
        }

        //ArrayList<String> mainMovingCommands = new ArrayList<>(Arrays.asList("STOP", "FORWARD", "FORWARD_STOP", "BACK", "BACK_STOP", "LEFT", "LEFT_STOP", "RIGHT", "RIGHT_STOP"));
    }

    public boolean getTag(String tag) {
        return queryTags.contains(tag);
    }

    public boolean isCameraSupported(){
        if(possibilitiesSettings.containsKey("camera")) {
            try {
                return possibilitiesSettings.get("camera") == 1;
            } catch (NullPointerException e){
                return false;
            }
        } else return false;
    }

    public boolean isMoveSupported(){
        if(possibilitiesSettings.containsKey("move")) {
            try {
                return possibilitiesSettings.get("move") == 1;
            } catch (NullPointerException e){
                return false;
            }
        } else return false;
    }

    public boolean isNeedPackageData(){
        if(possibilitiesSettings.containsKey("package_data")) {
            try {
                return possibilitiesSettings.get("package_data") == 1;
            } catch (NullPointerException e){
                return false;
            }
        } else return false;
    }

    //TODO
    //для package_data

    public boolean isNeedNewCommandButton(){
        return !newProtoCommands.isEmpty();
    }

    public Byte get(String key) {
        if(moveCodes.get(key)!=null)
            return moveCodes.get(key);
        else if(possibilitiesSettings.get(key)!=null)
            return possibilitiesSettings.get(key);
        else return newProtoCommands.get(key);
    }



    public HashMap<String, Byte> getNewDynamicCommands() {
        return newProtoCommands;
    }

    XmlPullParser prepareXpp(String name) throws IOException, XmlPullParserException {
        XmlPullParser xpp;
        if (name.equals(context.getResources().getString(R.string.TAG_default_protocol) + ".xml") || name.equals(context.getResources().getString(R.string.TAG_default_protocol))) {
            xpp = context.getResources().getXml(R.xml.arduino_default);
            return xpp;
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new
                File(context.getFilesDir() + File.separator + dbHelper.getFileName(name))));
        String read;
        StringBuilder builder = new StringBuilder();

        while ((read = bufferedReader.readLine()) != null) {
            Log.d("mLog", read);
            if (read.contains("<?xml"))
                continue;
            read = read.replaceAll(" ", "");
            builder.append(read);
            Log.d("mLog", read);
        }
        String codeText = builder.toString();
        bufferedReader.close();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        xpp = factory.newPullParser();
        xpp.setInput(new StringReader(codeText));

        return xpp;
    }

    public void parseCodes(String name) {
        String curName = "";
        try {
            XmlPullParser xpp = prepareXpp(name);
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    // начало документа
                    case XmlPullParser.START_TAG:
                        curName = xpp.getName();
                        queryTags.add(curName);
                        Log.d(LOG_TAG, "At " + curName);
                        break;
                    // содержимое тэга
                    case XmlPullParser.TEXT:
                        Log.d(LOG_TAG, xpp.getText());
                        String codeEl = xpp.getText();
                        if (codeEl.length() >=2 && codeEl.charAt(1) == 'x')
                            codeEl = codeEl.substring(2);
                        Byte xppCode = (byte) Integer.parseInt(codeEl, 16);
                        Log.d(LOG_TAG, "CODE " + curName + " " + xppCode);
                        if (mainLabels.contains(curName)) {
                            //основной список команд и кодов
                            moveCodes.put(curName, xppCode);
                        } else if(possibilitiesSettings.containsKey(curName)){
                            //список возможностей протокола
                            possibilitiesSettings.put(curName, xppCode);
                        } else {
                            //список неизвестных (новых) программе команд
                            newProtoCommands.put(curName, xppCode);
                        }
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
            Log.d(LOG_TAG, "END_DOCUMENT");

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public int stringXMLparser(String code) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            code = code.replaceAll(" ", "");
            code = code.replaceAll("\n", "");
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(code));

            String curName = "";
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        curName = xpp.getName();
                        break;
                    case XmlPullParser.TEXT:
                        if (curName.equals("length")) {
                            int len = Integer.parseInt(xpp.getText());
                        } else {
                            if (mainLabels.contains(curName)) {
                                String codeEl = xpp.getText();
                                if (codeEl.charAt(1) == 'x')
                                    codeEl = codeEl.substring(2);
                                Byte xppCode = (byte) ((Character.digit(codeEl.charAt(0), 16) << 4)
                                        + Character.digit(codeEl.charAt(1), 16));
                            }
                        }
                        break;

                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
            Log.d(LOG_TAG, "END_DOCUMENT");
        } catch (IOException e) {
            return 1;
        } catch (XmlPullParserException e) {
            return 2;
        }
        return 0;
    }
}
