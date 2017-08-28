package com.UtilityClasses;
import android.content.res.Resources;
import android.util.Log;

import com.rtomyj.Diary.R;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*
     Created by CaptainSaveAHoe on 6/15/17.
 */

public class XML {
    final static String XML_FILE = "Files.xml";

    private XML(){}


    public static synchronized void recordNewEntryToXML(final Resources resources, final File filesDir, final String filename,
                                                        final int imageCount, final String partnerString, final String jobString, final ArrayList<CharSequence> tagsArrList, final String locationName,
                                                        final double latitude, final double longitude, final boolean isFave, final boolean storedLocationBool, final int currMood){
        new Thread(new Runnable() {
            DocumentBuilder builder ;
            Document doc ;
            DOMSource source ;
            Node tagNode;
            NodeList tagList;
            String temp;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            @Override
            public void run() {
                try {

                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(new File(filesDir, XML_FILE));
                    source = new DOMSource(doc);

                    Element thisFile = doc.createElement("File");
                    thisFile.setAttribute("name", filename.trim().replaceAll(" ", "_") + ".txt");
                    thisFile.setAttribute("pics", Integer.toString(imageCount));
                    thisFile.setAttribute("tags", Integer.toString(tagsArrList.size()));

                    thisFile.setAttribute("partner", partnerString);
                    thisFile.setAttribute("occupation", jobString);

                    if (isFave)
                        thisFile.setAttribute("favoriteSelectedFile", "true");
                    else
                        thisFile.setAttribute("favoriteSelectedFile", "false");

                    CharSequence moods[] = resources.getStringArray(R.array.moods_arr);
                    thisFile.setAttribute("mood", moods[currMood].toString());
                    if ( storedLocationBool ) {
                        thisFile.setAttribute("latitude", Double.toString(latitude));
                        thisFile.setAttribute("longitude", Double.toString(longitude));

                        thisFile.setAttribute("locationName", locationName);
                    }

                    for (int index = 1; index <= tagsArrList.size(); index++) {
                        temp = "tag";
                        temp = temp + Integer.toString(index);
                        thisFile.setAttribute(temp, ((String) tagsArrList.get(index - 1)).replaceAll(" ", "_"));

                    }

                    // adds some spacing for readability
                    tagList = doc.getElementsByTagName("Files");
                    tagNode = tagList.item(0);

                    Text lineBreak = doc.createTextNode("\n\t");
                    tagNode.appendChild(lineBreak);
                    tagNode.appendChild(thisFile);


                    doc.normalizeDocument();
                    saveXML(source, new File(filesDir, XML_FILE));

                } catch (IOException | TransformerException | SAXException | ParserConfigurationException exception) {
                    Log.e("err adding entry to xml", exception.toString());

                }


            }
        }).start();
    }
    static private void saveXML(DOMSource source, File xmlFile) throws  TransformerException{
        StreamResult result = new StreamResult(xmlFile);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(source, result);
    }

    public static synchronized void updateEntryInXML(final Resources resources, final File filesDir, final String filename, final String oldFileName,
                                                     final int imageCount, final String partnerString, final String jobString, final ArrayList<CharSequence> tagsArrList, final String locationName,
                                                     final double latitude, final double longitude, final boolean isFave, final boolean storedLocationBool, final int currMood){

        new Thread(new Runnable() {
            DocumentBuilder builder ;
            Document doc ;
            DOMSource source ;
            String temp;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            boolean fileNameChanged = false, found = false;
            Element fileNode = null;
            @Override
            public void run() {

                try {

                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(new File(filesDir, "Files.xml"));
                    source = new DOMSource(doc);

                    Node parent = doc.getElementsByTagName("Files").item(0);
                    NodeList nodeList = parent.getChildNodes();

                    int length = nodeList.getLength();

                    if (oldFileName != null)        // if the user changed the filename then oldFileName is not null
                        fileNameChanged = true;

                    for(int i = 0; i < length; i++){
                        Node node = nodeList.item(i);
                        if ( !node.getNodeName().equals("#text")) {
                            fileNode = (Element) node;

                            if (fileNameChanged) {
                                assert oldFileName != null;
                                if (fileNode.getAttribute("name").equals(oldFileName.replaceAll(" ", "_") + ".txt")) {
                                    found = true;
                                    break;
                                }
                            } else {
                                if (fileNode.getAttribute("name").equals(filename.replaceAll(" ", "_") + ".txt")) {
                                    found = true;
                                    break;
                                }
                            }
                        }

                    }

                    if (found){
                        if (fileNameChanged)
                            fileNode.setAttribute("name", filename.trim().replaceAll(" ", "_") + ".txt");

                        fileNode.setAttribute("pics", Integer.toString(imageCount));
                        fileNode.setAttribute("tags", Integer.toString(tagsArrList.size()));

                        fileNode.setAttribute("partner", partnerString);
                        fileNode.setAttribute("occupation", jobString);


                        if (isFave)
                            fileNode.setAttribute("favoriteSelectedFile", "true");
                        else
                            fileNode.setAttribute("favoriteSelectedFile", "false");

                        CharSequence moods[] = resources.getStringArray(R.array.moods_arr);
                        fileNode.setAttribute("mood", moods[currMood].toString());

                        if (! storedLocationBool ) {
                            fileNode.setAttribute("latitude", Double.toString(latitude));
                            fileNode.setAttribute("longitude", Double.toString(longitude));

                            fileNode.setAttribute("locationName", locationName);
                        }
                        int tagCount = 1;
                        for (CharSequence tag: tagsArrList) {
                                temp = "tag";
                                temp = temp + Integer.toString(tagCount);
                                fileNode.setAttribute(temp, ((String)tag).replaceAll(" ", "_"));
                                tagCount++;
                        }

                    }

                    doc.normalizeDocument();
                    saveXML(source, new File(filesDir, XML_FILE));

                } catch (IOException | TransformerException | SAXException | ParserConfigurationException exception) {
                    Log.e("err adding entry to xml", exception.toString());
                }


            }
        }).start();

    }

    /*
    I am removing the file names from the ArrayList when the xml node with its name gets edited. This means that the calling method does not need to clear the ArrayList. Lists are passed by reference and not val.

    This method updates the xml metadata with changes to favorites. it is called near the end of the lifecycle of the activity with an adapter that allows changes to favorites. Before this method is called,
    any file names that are in a deleted ArrayList (a list of files the user deleted in the ListView) should be removed so that changes aren't done twice.

    fileNamesArr has strings that are filenames in the form of name_of_file.txt (extension and underscores instead of spaces.
     */

    public synchronized static void updateArrayOfFavesToXML(final File filesDir, final ArrayList<String> fileNamesArr){
        new Thread(new Runnable() {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document doc;

            DOMSource source;

            Node node;
            NodeList nodeList;
            Element element;

            @Override
            public void run() {

                try {
                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(new File(filesDir, XML_FILE));
                    source = new DOMSource(doc);

                    node = doc.getElementsByTagName("Files").item(0);
                    nodeList = node.getChildNodes();

                    int len = nodeList.getLength(), arraySize = fileNamesArr.size();

                    for (int i = 0; i < len; i++) {
                        node = nodeList.item(i);

                        if (! node.getNodeName().equals("#text")) {

                            element = (Element) node;
                            Log.e("File", element.getAttribute("name"));
                            String nameOfFile = element.getAttribute("name");

                            if (fileNamesArr.contains(nameOfFile)) {
                                if (element.getAttribute("favoriteSelectedFile").equals("false"))
                                    element.setAttribute("favoriteSelectedFile", "true");
                                else
                                    element.setAttribute("favoriteSelectedFile", "false");


                                -- arraySize;
                                if (arraySize == 0)
                                    break;
                            }
                        }


                    }

                    doc.normalize();
                    saveXML(source, new File(filesDir, XML_FILE));

                } catch (TransformerException | IOException | SAXException | ParserConfigurationException exception) {
                    Log.e("Err retrieving faves", exception.toString());
                    Log.e("Message", exception.getMessage());
                }

            }
        }).start();

    }


    static synchronized void deleteMultipleEntriesFromXML(final File filesDir, final ArrayList<String> deletedFileNamesArr){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File  XMLFile = new File(filesDir, XML_FILE);
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document  doc = builder.parse(XMLFile);
                    DOMSource domSource = new DOMSource(doc);

                    Node parentNode = doc.getElementsByTagName("Files").item(0);
                    NodeList nodeList = parentNode.getChildNodes();

                    int elementsRemoved = 0;

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        StringBuilder currFileNameOfXMLNode = new StringBuilder("");
                        Node cursor = nodeList.item(i);

                        if ( ! cursor.getNodeName().equals("#text")) {            // if it isn't a text node, proceed
                            Element element = (Element) cursor;
                            currFileNameOfXMLNode.append(element.getAttribute("name"));

                            // loops through deleted files to compare the current node/attribute = "name" value with the value of the current index of the array. If they match then the node gets deleted and the for loop should break;
                           for (String filename : deletedFileNamesArr) {
                                if (filename.equals(currFileNameOfXMLNode.toString())) {
                                    parentNode.removeChild(cursor);
                                    Node cursor2 = nodeList.item(i - 1);
                                    parentNode.removeChild(cursor2);
                                    elementsRemoved++;
                                    break;
                                }
                                if(deletedFileNamesArr.size() == elementsRemoved)
                                    break;
                            }
                        }
                    }

                    doc.normalize();
                    saveXML(domSource, new File(filesDir, XML_FILE));

                } catch (SAXException | IOException | ParserConfigurationException  | TransformerException exception) {
                    Log.e("Multiple delete err",exception.toString() );
                }
            }
        }).start();
    }

    static void deleteEntryFromXML(File filesDir , String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        File  XMLFile = new File(filesDir, "Files.xml");

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document  doc = builder.parse(XMLFile);
            TransformerFactory  tFactory = TransformerFactory.newInstance();
            StreamResult  streamResult = new StreamResult(XMLFile);
            DOMSource domSource = new DOMSource(doc);
            Transformer transformer = tFactory.newTransformer();

            Node parentNode = doc.getElementsByTagName("Files").item(0);
            NodeList nodeList = parentNode.getChildNodes();


            for (int i = 0; i < nodeList.getLength(); i++) {
                Node  cursor = nodeList.item(i);

                if ( ! cursor.getNodeName().equals("#text")) {
                    Element  element = (Element) cursor;

                    if (element.getAttribute("name").equals(filename.replaceAll(" ", "_") + ".txt")) {
                        parentNode.removeChild(cursor);
                        Node cursor2 = nodeList.item(i - 1);
                        WeakReference<Node> cursor2Weak = new WeakReference<>(cursor2);
                        parentNode.removeChild(cursor2Weak.get());
                        doc.normalize();
                        transformer.transform(domSource, streamResult);

                    }
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException | TransformerException exception) {
            Log.e("Deleting entry frm xml", exception.toString());
        }
    }



    // Loads the adapters data_activity_layout structures with the names of the files along with their tags, and whether they are favorite's or not.
    public static void getEntriesAdapterInfo(final ArrayList<String> sortedFilesArrList, final ArrayList<String> tag1ArrList, final ArrayList<String> tag2ArrList, final ArrayList<String> tag3ArrList,
                                final ArrayList<Boolean> favArrList, final File filesDir ) {
        new Thread(new Runnable() {
            DocumentBuilderFactory factory;
            DocumentBuilder builder;
            Document doc;

            Element element;
            Node node;
            NodeList nodeList;

            @Override
            public void run() {


                File filesXML = new File(filesDir, XML_FILE);

                factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringComments(true);

                try {
                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(filesXML);
                } catch (ParserConfigurationException parserException) {
                    Log.e("Error parsing xml", parserException.toString());
                } catch (SAXException saxException) {
                    Log.e("SAX exception", saxException.toString());
                } catch (IOException ioException) {
                    Log.e("IO exception", ioException.toString());
                }

                int index;
                StringBuilder currentTagName = new StringBuilder();

                if (doc != null)
                    try {
                        nodeList = doc.getElementsByTagName("Files");
                        node = nodeList.item(0);
                        nodeList = node.getChildNodes();
                        int nodeLen = nodeList.getLength();

                        for (int z = 0; z < nodeLen; z++) {
                            node = nodeList.item(z);
                            if (!node.getNodeName().equals("#text")) {
                                index = 0;
                                element = (Element) node;
                                for (String currentFileName : sortedFilesArrList) {
                                    if (element.getAttribute("name").equals(currentFileName)) {
                                        // If the amount of tags in the xml element "tags" is 0 then the tag Arrays will get populated with null
                                        // otherwise they will get populated in the for loop, along with the arraylist holding unique tags.
                                        int amountOfTags = Integer.parseInt(element.getAttribute("tags"));
                                        for (int x = 0; x < amountOfTags; x++) {
                                            currentTagName.delete(0, currentTagName.length());
                                            currentTagName.append(element.getAttribute("tag" + Integer.toString(x + 1)));
                                            if (x == 0) {
                                                tag1ArrList.set(index, currentTagName.toString());

                                            } else if (x == 1) {
                                                tag2ArrList.set(index, currentTagName.toString());

                                            } else if (x == 2) {
                                                tag3ArrList.set(index, currentTagName.toString());
                                            }
                                        }

                                        try {
                                            if (element.getAttribute("favoriteSelectedFile").equals("true"))
                                                favArrList.set(index, true);
                                        } catch (NullPointerException G) {
                                            Log.e("FaveEntries null ptr", G.toString());
                                        }
                                    }
                                    index++;
                                }
                            }
                            // if the file isn't entered int th Files.xml then a new instance will be made for it here.
                        }
                    } catch (IndexOutOfBoundsException outOfBounds) {
                        // if outof bounds add null
                        Log.e("Out of xml bounds", outOfBounds.toString());
                    }


            }
        }).start();



    }

    public static short getFaveNum(File filesDir){
        short faveNum = 0;
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc = null;

        Element element;
        Node node;
        NodeList nodeList;

        File filesXML = new File(filesDir, XML_FILE);

        factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(filesXML);
        }catch(ParserConfigurationException| SAXException | IOException exception){
            Log.e("Error getting fave num", exception.toString());
        }

        assert doc != null;
        nodeList = doc.getElementsByTagName("Files");
        node = nodeList.item(0);
        nodeList = node.getChildNodes();
        int nodeLen = nodeList.getLength();

        for (int z = 0; z < nodeLen; z++) {
            node = nodeList.item(z);
            if (!node.getNodeName().equals("#text")) {
                element = (Element) node;
                if (element.getAttribute("favoriteSelectedFile").equals("true"))
                    faveNum++;
            }
        }

        return faveNum;
    }


    // Loads the adapters data_activity_layout structures with the names of the files along with their tags, and whether they are favorite's or not.
    public static void getFaveEntries(final ArrayList<String> filenames, final ArrayList<String> tag1ArrList, final ArrayList<String> tag2ArrList, final ArrayList<String> tag3ArrList
            , final ArrayList<Boolean >faveArrList, final File filesDir )  throws ParserConfigurationException, SAXException, IOException{

        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc;

        Element element;
        Node node;
        NodeList nodeList;


        File filesXML = new File(filesDir, XML_FILE);

        factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);

        builder = factory.newDocumentBuilder();
        doc = builder.parse(filesXML);

        StringBuilder currentTagName = new StringBuilder();
        byte  amountOfTags;


        nodeList = doc.getElementsByTagName("Files");
        node = nodeList.item(0);
        nodeList = node.getChildNodes();
        int nodeLen = nodeList.getLength();

        for (int z = 0; z < nodeLen; z++) {
            node = nodeList.item(z);
            if (!node.getNodeName().equals("#text")) {
                element = (Element) node;
                if (element.getAttribute("favoriteSelectedFile").equals("true")) {
                    faveArrList.add(true);
                    filenames.add(element.getAttribute("name"));
                    // If the amount of tags in the xml element "tags" is 0 then the tag Arrays will get populated with null
                    // otherwise they will get populated in the for loop, along with the ArrayList holding unique tags.

                    amountOfTags = Byte.parseByte(element.getAttribute("tags"));
                    switch (amountOfTags){
                        case 0:
                            tag1ArrList.add("");
                        case 1:
                            tag2ArrList.add("");
                        case 2:
                            tag3ArrList.add("");
                    }
                    getTags: for (byte x = 0; x < amountOfTags; x++) {
                        currentTagName.delete(0, currentTagName.length());
                        currentTagName.append(element.getAttribute("tag" + Integer.toString(x + 1)));
                        String currentTag = currentTagName.toString();
                        switch (x){
                            case 0:
                                tag1ArrList.add(currentTag);
                                break;
                            case 1:
                                tag2ArrList.add(currentTag);
                                break;
                            case 2:
                                tag3ArrList.add(currentTag);
                                break;
                            default:
                                break getTags;
                        }

                    }
                }
            }
        }
        // if the file isn't entered int th Files.xml then a new instance will be made for it here.





    }


    public static void getChosenTagEntries(final ArrayList<String> filenamesArrList, final ArrayList<String> tag1ArrList, final ArrayList<String> tag2ArrList, final ArrayList<String> tag3ArrList,
                                           final ArrayList<Boolean> favesArrList, final File filesDir , final String chosenTagName) throws IndexOutOfBoundsException{
        chosenTagName.replaceAll(" ", "_");

        new Thread(new Runnable() {

            DocumentBuilderFactory factory;
            DocumentBuilder builder;
            Document doc;

            Element element;
            Node node;
            NodeList nodeList;

            @Override
            public void run() {

                File filesXML = new File(filesDir, XML_FILE);

                factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringComments(true);
                try {
                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(filesXML);
                }catch(ParserConfigurationException| SAXException | IOException exception){
                    Log.e("Error parsing xml", exception.toString());
                }

                StringBuilder currentTagName = new StringBuilder(), strBuilder;
                int currentDataSet = 0;

                nodeList = doc.getElementsByTagName("Files");
                node = nodeList.item(0);
                nodeList = node.getChildNodes();
                int nodeLen = nodeList.getLength();

                for (int z = 0; z < nodeLen; z++) {
                    node = nodeList.item(z);
                    if (!node.getNodeName().equals("#text")) {
                        element = (Element) node;
                        int tagAmount = Integer.parseInt(element.getAttribute("tags"));

                        for (int index = 1; index <= tagAmount; index++) {
                            strBuilder = new StringBuilder("tag");
                            strBuilder.append(Integer.toString(index));

                            if (element.getAttribute(strBuilder.toString()).equals(chosenTagName)) {
                                filenamesArrList.set(currentDataSet,  element.getAttribute("name"));
                                if (element.getAttribute("favoriteSelectedFile").equals("true"))
                                    favesArrList.set(currentDataSet, true);

                                // If the amount of tags in the xml element "tags" is 0 then the tag Arrays will get populated with null
                                // otherwise they will get populated in the for loop, along with the ArrayList holding unique tags.

                                getTags:
                                for (byte x = 0; x < tagAmount; x++) {
                                    currentTagName.delete(0, currentTagName.length());
                                    currentTagName.append(element.getAttribute("tag" + Integer.toString(x + 1)));
                                    String currentTag = currentTagName.toString();

                                    switch (x) {
                                        case 0:
                                            tag1ArrList.set(currentDataSet, currentTag);
                                            break;
                                        case 1:
                                            tag2ArrList.set(currentDataSet, currentTag);
                                            break;
                                        case 2:
                                            tag3ArrList.set(currentDataSet, currentTag);
                                            break;
                                        default:
                                            break getTags;
                                    }
                                }
                                currentDataSet ++;
                            }
                        }
                    }
                }
                // if the file isn't entered int th Files.xml then a new instance will be made for it here.

            }
        }).start();



    }

    public static void getTagsFromXML(ArrayList<String> unsortedTagsArrList, ArrayList<Byte> unsortedTagNumArrList, ArrayList<String> fileNames, File filesDir){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        File xml = new File(filesDir, XML_FILE);

            try {
                Document doc;
                Element currentElement;
                Node node;
                NodeList nodeList;
                DocumentBuilder builder = factory.newDocumentBuilder();
                doc = builder.parse(xml);

                nodeList = doc.getElementsByTagName("Files");
                node = nodeList.item(0);
                currentElement = (Element) node;

                nodeList = currentElement.getChildNodes();

                int XML_listSize = nodeList.getLength(), Array_listSiz = 0;

                for (int z = 0; z < XML_listSize; z++) {
                    node = nodeList.item(z);

                    if ( ! node.getNodeName().equals("#text")) {
                        currentElement = (Element) node;

                        // if current node isn't for the file in question, it searches through all its tags and adds those that are unique to suggestionsArrList

                        int tagNumInCurrentElement = Integer.parseInt(currentElement.getAttribute("tags"));

                        for (int j = 0; j < tagNumInCurrentElement; j++){
                            String currentTag = ((Element) node).getAttribute("tag" + Integer.toString(j + 1).replaceAll("_", " "));
                            if (!unsortedTagsArrList.contains(currentTag) ){
                                unsortedTagsArrList.add(currentTag);
                                unsortedTagNumArrList.add((byte) 1);
                                Log.e("heee", currentTag);

                                fileNames.add(((Element) node).getAttribute("name"));
                                Array_listSiz ++;

                            } else {
                                for (int x = 0; x < Array_listSiz; x++) {
                                    if (unsortedTagsArrList.get(x).equals(currentTag)) {

                                        unsortedTagNumArrList.set(x, (byte) (unsortedTagNumArrList.get(x) +  1));
                                    }
                                }
                            }
                    }
                    }
                }
            } catch (IOException | ParserConfigurationException  | SAXException exception) {
                Log.e("Err parsing tags", exception.toString());

            }


    }




}
