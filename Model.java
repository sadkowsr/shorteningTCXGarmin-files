import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




public class Model {

    public static void search(final String pattern, final File folder, List<String> result,boolean recurs) {
        for (final File f : folder.listFiles()) {

            if (recurs && f.isDirectory()) {
                search(pattern, f, result,true);
            }

            if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    result.add(f.getAbsolutePath());
                }
            }

        }
    }
private static final int MAX_LENGTH_COURSE_NAME_FOR_GARMIN = 10;

    public static String getBestString(String inputString){

        final String[][] directory =  {

                {"Right","Right ->"},
                {"Left","<- Left"},
                {"U-turn","U Turn <>"},
                {"Straight","Straight ^"},


                {"Ostro w prawo","OstroPrawa"},
                {"Ostro w lewo","Ostro lewa"},
                {"Lekko w prawo","LekkoPrawa"},
                {"Lekko w lewo","Lekko lewa ^"},

                {"Prawo","W prawo ->"},
                {"Lewo","<- W lewo"},
                {"Zawróć","Zawróć <>"},
                {"Prosto","Prosto /^\\"}

        };
        for(int i=0;i<directory.length;i++) {
            if (inputString.toLowerCase().contains(directory[i][0].toLowerCase())) {
                return directory[i][1];
            }
        }
        return inputString;
    }

    public static String shorterString(String inputString,int maxLenght){
        if(inputString==null)
            return null;

        //check lenght... if ok return input string

        //delete samogloski
        //
        //if ok return input string without samogloski


        if(inputString.length()<maxLenght){
            maxLenght=inputString.length();
        }


        String shortString = inputString.substring(0,maxLenght);

        //no latin letter
        /*StringBuilder outputText = new StringBuilder();
        int L = shortString.length();
        for (int I = 0; I < L; I++)
        {
            switch (shortString.charAt(I))
            {
                case 'ą': outputText.append('a'); break;
                case 'ć': outputText.append('c'); break;
                case 'ę': outputText.append('e'); break;
                case 'ł': outputText.append('l'); break;
                case 'ń': outputText.append('n'); break;
                case 'ó': outputText.append('o'); break;
                case 'ś': outputText.append('s'); break;
                case 'ź': outputText.append('z'); break;
                case 'ż': outputText.append('z'); break;
                case 'Ą': outputText.append('A'); break;
                case 'Ć': outputText.append('C'); break;
                case 'Ę': outputText.append('E'); break;
                case 'Ł': outputText.append('L'); break;
                case 'Ń': outputText.append('N'); break;
                case 'Ó': outputText.append('O'); break;
                case 'Ś': outputText.append('S'); break;
                case 'Ź': outputText.append('Z'); break;
                case 'Ż': outputText.append('Z'); break;
                default: outputText.append(shortString.charAt(I));
                break;
            }
        }
         shortString = outputText.toString();
        */
        return shortString;


    }
     public static void main(String argv[]) {



        File folder = null;
        try {
            folder = new File(new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> result = new ArrayList<>();

        search(".*\\.tcx", folder, result,false);

        for (String s : result) {
            analyse(s, "/output/",MAX_LENGTH_COURSE_NAME_FOR_GARMIN);
        }



    }

    static void analyse(String inputFilename, String outputDirectory, int maxSize) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(inputFilename));
            Node trainingDataBase = doc.getFirstChild();

            NodeList documents = trainingDataBase.getChildNodes();
            NodeList courses = null;


            for (int i = 0; i < documents.getLength(); i++) {
                Node n = documents.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE &&
                        "Courses".equals(n.getNodeName())) {
                    courses = n.getChildNodes();
                    break;
                }
            }
            if (courses == null)
                return;

            NodeList course = null;
            for (int i = 0; i < courses.getLength(); i++) {
                Node n = courses.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE &&
                        "Course".equals(n.getNodeName())) {
                    course = n.getChildNodes();
                    break;
                }
            }
            if (course == null)
                return;

            List<NodeList> coursePointList = new ArrayList<>();
            for (int i = 0; i < course.getLength(); i++) {
                Node n = course.item(i);
                if ((n.getNodeType() == Node.ELEMENT_NODE) &&
                        "CoursePoint".equals(n.getNodeName())) {
                    coursePointList.add(n.getChildNodes());
                }
            }
            if (coursePointList.isEmpty())
                return;

            for(NodeList cp:coursePointList) {
                for (int i = 0; i < cp.getLength(); i++) {
                    Node n = cp.item(i);
                    if ("Name".equals(n.getNodeName())) {
                        n.setTextContent(
                                shorterString(
                                        getBestString(n.getTextContent())
                                        ,maxSize)
                        );
                        //System.out.println(n.getTextContent());
                    }

                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);


            //String dir = new File(inputFilename).getParent();
            String filename = new File(inputFilename).getName();

            if(!Files.exists(Paths.get(outputDirectory))){
                new File(outputDirectory+"/").mkdirs();
            }

            StreamResult consoleResult = new StreamResult(new File(outputDirectory+"/"+filename));
            System.out.println("Create:"+outputDirectory+"/"+filename);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}