package com.UtilityClasses;

import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.Log;

import com.rtomyj.Diary.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by CaptainSaveAHoe on 6/15/17.
 */

public class AnimusXML {
    private AnimusXML(){}


    /*
	New users are assigned new files for tagging, indexing, and syncing purposes. Method checks to see if those files are made, if not then they are generated for the user.
	 */
    public static void checkForAppFiles(final File filesDir, final AssetManager assets, final ContentResolver contentResolver){
        new Thread(){
            String line;

            InputStreamReader inputStream;
            WeakReference<InputStreamReader> inputStreamWeak;
            BufferedReader reader;
            WeakReference<BufferedReader> readerWeak;
            BufferedWriter writer;
            WeakReference<BufferedWriter> writerWeak;

            public void run(){
                File filesXML = new File(filesDir, "Files.xml");
                if (! filesXML.exists() ) {			 // creats files file. It indexes all the files along with their associated tags, pictures and other info.
                    try {
                        filesXML.createNewFile();

                        inputStream = new InputStreamReader(assets.open("Files.xml"));
                        inputStreamWeak = new WeakReference<>(inputStream);

                        reader = new BufferedReader(inputStreamWeak.get());
                        readerWeak = new WeakReference<>(reader);

                        writer  = new BufferedWriter(new FileWriter(filesXML));
                        writerWeak = new WeakReference<>(writer);

                        line = (readerWeak.get().readLine());
                        while (line != null) {

                            writerWeak.get().write(line + "\n");
                            line = (readerWeak.get().readLine());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try { // closes streams in order they were open in case there's an exception to catch. Minimizes the risk of memory leak.
                            inputStreamWeak.get().close();
                            readerWeak.get().close();
                            writerWeak.get().close();
                        }catch (IOException ignored){

                        }
                    }
                }

                File changesXML = new File(filesDir, "Changes_" + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) + ".xml");
                if (! changesXML.exists() ) {		// creates a changes xml file

                    try {       // writes file for changes. Used for suyncing
                        changesXML.createNewFile();
                        inputStream = new InputStreamReader(assets.open("Changes.xml"));
                        inputStreamWeak = new WeakReference<>(inputStream);

                        reader = new BufferedReader(inputStreamWeak.get());
                        readerWeak = new WeakReference<>(reader);

                        writer  = new BufferedWriter(new FileWriter(changesXML));
                        writerWeak = new WeakReference<>(writer);

                        line =(readerWeak.get().readLine());
                        while (line != null) {
                            writerWeak.get().write(line + "\n");
                            line = (readerWeak.get().readLine());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try { // closes streams in order they were open in case theres an exception to catch. Minimizes the risk of memory leak.
                            inputStreamWeak.get().close();
                            readerWeak.get().close();
                            writerWeak.get().close();
                        }catch (IOException ignored){

                        }
                    }
                }

            }
        }.start();
    }

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
                    doc = builder.parse(new File(filesDir, "Files.xml"));
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

                    CharSequence moods[] = resources.getStringArray(R.array.moods_for_xml);
                    thisFile.setAttribute("mood", moods[currMood].toString());
                    if (! storedLocationBool ) {
                        thisFile.setAttribute("latitude", Double.toString(latitude));
                        thisFile.setAttribute("longitude", Double.toString(longitude));

                        thisFile.setAttribute("locationName", locationName);
                    }

                    for (int i = 0; i < tagsArrList.size(); i++) {

                        try {
                            temp = "tag";
                            temp = temp + Integer.toString(i + 1);
                            thisFile.setAttribute(temp, ((String) tagsArrList.get(i)).replaceAll(" ", "_"));
                        } catch (Exception ignored) {

                        }
                    }

                    // adds some spacing for readability
                    tagList = doc.getElementsByTagName("Files");
                    tagNode = tagList.item(0);
                    Text tab = doc.createTextNode("\t\t");
                    tagNode.appendChild(tab);
                    tagNode.appendChild(thisFile);

                    Text lineBreak = doc.createTextNode("\n\t");
                    tagNode.appendChild(lineBreak);



                    doc.normalizeDocument();
                    saveXML(source, new StreamResult(new File(filesDir, "Files.xml")));

                } catch (IOException | TransformerException | SAXException | ParserConfigurationException exception) {
                    Log.e("err adding entry to xml", exception.toString());
                }


            }
        }).start();
    }
    static private void saveXML(DOMSource source, StreamResult result) throws  TransformerException{
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

                        CharSequence moods[] = resources.getStringArray(R.array.moods_for_xml);
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
                    saveXML(source, new StreamResult(new File(filesDir, "Files.xml")));

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

    public synchronized static void updateFavesToXML(final File filesDir, final ArrayList<String> fileNamesArr){
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
                    doc = builder.parse(new File(filesDir, "Files.xml"));
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
                    saveXML(source, new StreamResult(new File(filesDir, "Files.xml")));

                } catch (TransformerException | IOException | SAXException | ParserConfigurationException exception) {
                    Log.e("Err retrieving faves", exception.toString());
                    Log.e("Message", exception.getMessage());
                }

            }
        }).start();

    }


    static synchronized void deleteMultipleEntriesFromXML(final File filesDir, final ArrayList<String> deletedFileNamesArr){
        new Thread(new Runnable() {
            File  XMLFile = new File(filesDir, "Files.xml");
            WeakReference<File> xmlFileWeak = new WeakReference<>(XMLFile);

            @Override
            public void run() {

                try {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();


                    Document  doc = builder.parse(xmlFileWeak.get());
                    TransformerFactory  tFactory = TransformerFactory.newInstance();

                    WeakReference<Document> docWeak = new WeakReference<>(doc);
                    WeakReference<TransformerFactory> tFactoryWeak = new WeakReference<>(tFactory);

                    StreamResult  streamResult = new StreamResult(xmlFileWeak.get());
                    DOMSource domSource = new DOMSource(docWeak.get());
                    WeakReference<StreamResult> streamResultWeak = new WeakReference<>(streamResult);
                    WeakReference<DOMSource> domSourceWeak = new WeakReference<>(domSource);

                    xmlFileWeak.clear();


                    Transformer transformer = tFactoryWeak.get().newTransformer();
                    WeakReference<Transformer> transformerWeak = new WeakReference<>(transformer);

                    Node parentNode = docWeak.get().getElementsByTagName("Files").item(0);
                    WeakReference<Node> parentNodeWeak = new WeakReference<>(parentNode);

                    NodeList nodeList = parentNodeWeak.get().getChildNodes();
                    WeakReference<NodeList> nodeListWeak = new WeakReference<>(nodeList);


                    for (int i = 0; i < nodeListWeak.get().getLength(); i++) {
                        Node  cursor = nodeListWeak.get().item(i);
                        WeakReference<Node> cursorWeak = new WeakReference<>(cursor);

                        if ( ! cursorWeak.get().getNodeName().equals("#text")) {            // if it isn't a text node, proceed
                            Element element = (Element) cursorWeak.get();
                            WeakReference<Element> elementWeak = new WeakReference<>(element);
                            String currFileNameOfXMLNode = elementWeak.get().getAttribute("name");

                            // loops through deleted files to compare the current node/attribute = "name" value with the value of the current index of the array. If they match then the node gets deleted and the for loop should break;
                           for (String filename : deletedFileNamesArr) {
                                if (currFileNameOfXMLNode.equals(filename)) {
                                    parentNodeWeak.get().removeChild(cursorWeak.get());
                                    Node cursor2 = nodeListWeak.get().item(i - 1);
                                    WeakReference<Node> cursor2Weak = new WeakReference<>(cursor2);
                                    parentNodeWeak.get().removeChild(cursor2Weak.get());
                                    deletedFileNamesArr.remove(filename);
                                    break;
                                }
                                if(deletedFileNamesArr.size() == 0)
                                    break;
                            }
                        }
                    }

                    docWeak.get().normalize();
                    try {
                        transformerWeak.get().transform(domSourceWeak.get(), streamResultWeak.get());
                    } catch (TransformerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } catch (SAXException | IOException | ParserConfigurationException | TransformerConfigurationException exception) {
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



    // Loads the adapters data structures with the names of the files along with their tags, and whether they are favorite's or not.
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


                File filesXML = new File(filesDir, "Files.xml");

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

                Log.e("Size of array", Integer.toString(tag1ArrList.size()));

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

        File filesXML = new File(filesDir, "Files.xml");

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


    // Loads the adapters data structures with the names of the files along with their tags, and whether they are favorite's or not.
    public static void getFaveEntries(final ArrayList<String> filenames, final ArrayList<String> tag1ArrList, final ArrayList<String> tag2ArrList, final ArrayList<String> tag3ArrList,
                                             final ArrayList<Boolean> favArrList, final File filesDir ) throws IndexOutOfBoundsException{
        new Thread(new Runnable() {
            DocumentBuilderFactory factory;
            DocumentBuilder builder;
            Document doc;

            Element element;
            Node node;
            NodeList nodeList;

            @Override
            public void run() {

                File filesXML = new File(filesDir, "Files.xml");

                factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringComments(true);
                try {
                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(filesXML);
                }catch(ParserConfigurationException| SAXException | IOException exception){
                    Log.e("Error parsing xml", exception.toString());
                }

                StringBuilder currentTagName = new StringBuilder();
                byte faveIndex = 0, amountOfTags;


                nodeList = doc.getElementsByTagName("Files");
                node = nodeList.item(0);
                nodeList = node.getChildNodes();
                int nodeLen = nodeList.getLength();

                for (int z = 0; z < nodeLen; z++) {
                    node = nodeList.item(z);
                    if (!node.getNodeName().equals("#text")) {
                        element = (Element) node;
                        if (element.getAttribute("favoriteSelectedFile").equals("true")) {
                            filenames.set(faveIndex, element.getAttribute("name"));
                            // If the amount of tags in the xml element "tags" is 0 then the tag Arrays will get populated with null
                            // otherwise they will get populated in the for loop, along with the ArrayList holding unique tags.

                            amountOfTags = Byte.parseByte(element.getAttribute("tags"));
                            getTags: for (byte x = 0; x < amountOfTags; x++) {
                                currentTagName.delete(0, currentTagName.length());
                                currentTagName.append(element.getAttribute("tag" + Integer.toString(x + 1)));
                                String currentTag = currentTagName.toString();

                                switch (x){
                                    case 0:
                                        tag1ArrList.set(faveIndex, currentTag);
                                        break;
                                    case 1:
                                        tag2ArrList.set(faveIndex, currentTag);
                                        break;
                                    case 2:
                                        tag3ArrList.set(faveIndex, currentTag);
                                        break;
                                    default:
                                        break getTags;
                                }
                            }
                            faveIndex ++;
                        }
                    }
                }
                // if the file isn't entered int th Files.xml then a new instance will be made for it here.

            }
        }).start();



    }

    public static void getTagsFromXML(ArrayList<String> unsortedTagsArrList, ArrayList<Byte> unsortedTagNumArrList, ArrayList<String> fileNames, File filesDir){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        File xml = new File(filesDir, "Files.xml");

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
