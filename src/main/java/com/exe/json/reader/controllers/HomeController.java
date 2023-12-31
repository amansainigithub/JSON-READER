package com.exe.json.reader.controllers;

import com.exe.json.reader.helper.Helper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("jsonReader")
public class HomeController {

    private StringBuilder  pcm;
    private static final String ONE_SPACE = " ";
    private static final String  PUBLIC = "public ";
    private static final String  PUBLIC_CLASS = PUBLIC + ONE_SPACE + "class" +ONE_SPACE;
    private  static final String CURLY_BRACES_OPEN = " { ";
    private  static final String CURLY_BRACES_CLOSE = " } ";
    private  static final String DOUBLE_CURLY_BRACES_OPEN = " {{ ";
    private  static final String DOUBLE_CURLY_BRACES_CLOSE = " }} ";
    private  static final String PRIVATE_VARIABLE_MAKER = "private String ";
    private  static final String PRIVATE_OBJECT_VARIABLE_MAKER = "private ";
    private  static final String SEMI_COLON = " ; ";
    private  static final String LIST_VAR_OPEN="List< ";
    private  static final String LIST_VAR_CLOSE=" > ";
    private  static final String NEXT_LINE = " \n ";
    private static final String PACKAGE_KEYWORD = "package ";
    private ArrayList<String> deepList = null;
    private Map<String,String> classProcessMakerList= null;
    private static final String  PACKAGE_PATH= "src/main/java/com/exe/json/reader/entities";
    private static final String  PACKAGE_NAME= "com.exe.json.reader.entities";
    private static final String IMPORT = "import ";
    private static final String IMPORT_LIST = IMPORT + " java.util.List;";

    private static final String DOT = ".";

    private static final String BACK_SLASH = "/";

    private static final String VOID = "void"+ONE_SPACE;
    private static final String SG_STRING = "String" + ONE_SPACE;
    private static final String PARENTHESIS_OPEN = "(";
    private static final String PARENTHESIS_CLOSE = ")";
    private static final String GET = "get" ;
    private static final String ARRAY_OPEN = "[" ;
    private static final String ARRAY_CLOSE = "]" ;
    private static final String RETURN = "return ";
    private static final String EVERY_LINE_SPLITTER_PATTERN = "\\r?\\n|\\r";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_BLACK = "\u001B[30m";



    @PostMapping("/jp")
    public String JsonParserWithCom(@RequestBody String jsonP) {
        //Prepare to fly
        prepareToFly();

        pcm = new StringBuilder();
        classProcessMakerList = new HashMap<>();
        deepList = new ArrayList<>();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonP);
        JsonObject obj = element.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {

            if ((obj.get(entry.getKey()).isJsonObject())) {
                pcm.append("{" + entry.getKey() + "}" + NEXT_LINE);
                deepList.add(entry.getKey());
                classMaker(entry.getKey());
                isJsonObject(obj.get(entry.getKey()));
                pcm.append("{" + entry.getKey() + "}" + NEXT_LINE);


            } else if (obj.get(entry.getKey()).isJsonArray()) {
                pcm.append("{" + entry.getKey() + "}" + NEXT_LINE);
                deepList.add(entry.getKey());
                classMaker(entry.getKey());
                isJsonArray(obj.get(entry.getKey()));
                pcm.append("{" + entry.getKey() + "}" + NEXT_LINE);
            } else if ((obj.get(entry.getKey()).isJsonPrimitive())) {
                System.out.println("JSON PREMITIVE :: " + entry.getKey());
            }
        }

        //calling Split Classes
        return this.splitClasses();
    }


    public String splitClasses()
    {
        //Reverse ArrayList [Helping to creating a class with proper manner]
        Collections.reverse(deepList);

        String stringBuilderVar = pcm.toString();
        System.out.print(ANSI_BLACK + "REVERSE DEEP-LIST :: ");
        System.out.println(ANSI_GREEN  + deepList);
        String splitSTR = null;
        for (String classFinder : deepList) {
            String splitClass[] = stringBuilderVar.split("[{]" + classFinder + "[}]");
            splitSTR = splitClass[1];
            stringBuilderVar = stringBuilderVar.replace(splitSTR, "");
            stringBuilderVar = stringBuilderVar.replace("{" + classFinder + "}", "");
            classProcessMakerList.put( classFinder , splitSTR );
        }

        System.out.println(ANSI_BLACK + "************************");
        //System.out.println("ClassMakerProcessList START" + classProcessMakerList.toString() + " ClassMakerProcessList END ");
        this.classMaker();
        //this.generateSetterGetter();
        return classProcessMakerList.toString();
    }




    public void classMaker()
    {
        System.out.println(ANSI_BLACK + "Class Name :: " + ANSI_GREEN + getClass().getName());
        System.out.println(ANSI_BLACK + "Package name ::" + ANSI_GREEN + PACKAGE_NAME);
        System.out.println(ANSI_BLACK + "Total classes ::" + ANSI_GREEN + deepList.size());

        for(String classes_key : classProcessMakerList.keySet())
        {
            try {
                     // Creates a Writer using FileWriter
                     FileWriter fileWriter = new FileWriter(PACKAGE_PATH + File.separator + classes_key + ".java");
                     // Writes the program to file

                fileWriter.write(PACKAGE_KEYWORD + PACKAGE_NAME + SEMI_COLON + NEXT_LINE);

                     if(classProcessMakerList.get(classes_key).contains("List"))
                     {
                         fileWriter.write(IMPORT_LIST + NEXT_LINE);
                     }
                        String removeVarBraces =  classProcessMakerList.get(classes_key).
                                                    replace(DOUBLE_CURLY_BRACES_OPEN,"").
                                                    replace(DOUBLE_CURLY_BRACES_CLOSE,"").
                                                    replace(ARRAY_OPEN,"").
                                                    replace(ARRAY_CLOSE,"");


                        String spy =  classProcessMakerList.get(classes_key);
                        String every_line_splitter[] = spy.split(EVERY_LINE_SPLITTER_PATTERN);

                for(int i = 0 ;i < every_line_splitter.length -1 ; i++)
                {
                    if(every_line_splitter[i].contains(ARRAY_OPEN)
                       && every_line_splitter[i].contains(ARRAY_CLOSE))
                    {
                        int startingIndex = every_line_splitter[i].indexOf(ARRAY_OPEN.trim());
                        int closingIndex = every_line_splitter[i].indexOf(ARRAY_CLOSE.trim());
                        String bracesValue = every_line_splitter[i].substring(startingIndex + 1, closingIndex);
                        fileWriter.write(IMPORT + ONE_SPACE + PACKAGE_NAME +DOT + bracesValue + SEMI_COLON);
                    }
                }

                fileWriter.write(removeVarBraces + NEXT_LINE );

                        for(int i = 0 ;i < every_line_splitter.length -1 ; i++)
                        {
                            if(every_line_splitter[i].contains(DOUBLE_CURLY_BRACES_OPEN)
                                && every_line_splitter[i].contains(DOUBLE_CURLY_BRACES_CLOSE)
                                || every_line_splitter[i].contains(ARRAY_OPEN)
                                && every_line_splitter[i].contains(ARRAY_CLOSE) )
                            {
                                if(every_line_splitter[i].contains(ARRAY_OPEN)
                                   && every_line_splitter[i].contains(ARRAY_CLOSE))
                                {
                                    int startingIndex = every_line_splitter[i].indexOf(ARRAY_OPEN.trim());
                                    int closingIndex = every_line_splitter[i].indexOf(ARRAY_CLOSE.trim());
                                    String bracesValue = every_line_splitter[i].substring(startingIndex + 1, closingIndex);
                                    this.makeGetter(bracesValue.trim(), fileWriter, ARRAY_OPEN);

                                }else if(every_line_splitter[i].contains(DOUBLE_CURLY_BRACES_OPEN)
                                        && every_line_splitter[i].contains(DOUBLE_CURLY_BRACES_CLOSE))
                                {
                                    int startingIndex = every_line_splitter[i].indexOf(DOUBLE_CURLY_BRACES_OPEN.trim());
                                    int closingIndex = every_line_splitter[i].indexOf(DOUBLE_CURLY_BRACES_CLOSE.trim());
                                    String bracesValue = every_line_splitter[i].substring(startingIndex + 2, closingIndex);
                                    this.makeGetter(bracesValue.trim(), fileWriter, DOUBLE_CURLY_BRACES_OPEN);

                                 }

                            }
                        }

                        fileWriter.write(CURLY_BRACES_CLOSE);
                    // Closes the writer
                    fileWriter.close();
                    System.out.println(ANSI_BLACK + "File Created [ NAME: " + ANSI_GREEN + classes_key
                                       + " => " + ANSI_BLUE + "SUCCESS" + " ] ");
            }
            // Catch block to handle if exception occurs
            catch (IOException e) {
                // Print the exception
                System.out.print(e.getMessage());
            }
        }
    }


    public void makeGetter(String bracesValue , FileWriter fileWriter , String identifier) throws IOException {

            if(identifier.equals(DOUBLE_CURLY_BRACES_OPEN)){
                fileWriter.write(NEXT_LINE + PUBLIC + SG_STRING + GET + bracesValue + PARENTHESIS_OPEN + PARENTHESIS_CLOSE +
                        CURLY_BRACES_OPEN + RETURN + bracesValue + SEMI_COLON + CURLY_BRACES_CLOSE );
            }else if(identifier.equals(ARRAY_OPEN)){

                fileWriter.write(NEXT_LINE + PUBLIC + this.firstLetterCapital(bracesValue) + NEXT_LINE + GET + bracesValue + PARENTHESIS_OPEN + PARENTHESIS_CLOSE +
                        CURLY_BRACES_OPEN + RETURN + bracesValue.toLowerCase() + SEMI_COLON + CURLY_BRACES_CLOSE );
            }

           }

    public void isJsonObject(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {

            //inside DEEP ARR
            if((obj.get(entry.getKey()).isJsonArray()))
            {
                privateListMakerVariable(entry.getKey());
                pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                deepList.add(entry.getKey());
                classMaker(entry.getKey());
                isJsonArray(obj.get(entry.getKey()));
                pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
            }

            //inside DEEP OBJ
            else if((obj.get(entry.getKey()).isJsonObject()))
            {
                privateObjectMakerVariable(entry.getKey());
                deepList.add(entry.getKey());
                pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                classMaker(entry.getKey());
                isJsonObject(obj.get(entry.getKey()));
                pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
            }else {
                //APPEND KEYS TO PRIVATE VAR [DEEP OBJECT]
                privateVariableMaker(entry.getKey());
            }
        }
    }


    public void isJsonArray(JsonElement element) {
        JsonArray jsonArray = element.getAsJsonArray();
        for(JsonElement ele : jsonArray) {
            JsonObject obj = ele.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {

                //inside DEEP ARR
                if((obj.get(entry.getKey()).isJsonArray()))
                {
                    privateListMakerVariable(entry.getKey());
                    pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                    deepList.add(entry.getKey());
                    classMaker(entry.getKey());
                    isJsonArray(obj.get(entry.getKey()));
                    pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                }
                //inside DEEP OBJ
                else if((obj.get(entry.getKey()).isJsonObject()))
                {
                    privateObjectMakerVariable(entry.getKey());
                    deepList.add(entry.getKey());
                    pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                    classMaker(entry.getKey());
                    isJsonArray(obj.get(entry.getKey()));
                    pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                }
                else
                {
                    //APPEND KEYS TO PRIVATE VAR [DEEP OBJECT]
                    privateVariableMaker(entry.getKey());
                }
            }
        }
    }



    public void classMaker(String className)
    {
        pcm.append( PUBLIC_CLASS + Helper.firstLetterCap(className.toLowerCase()));
        curlsBracesOpening();
    }
    public void curlsBracesOpening()
    {
        pcm.append( NEXT_LINE + CURLY_BRACES_OPEN + NEXT_LINE );
    }
    public void curlsBracesClosing()
    {
        pcm.append( NEXT_LINE + CURLY_BRACES_CLOSE + NEXT_LINE );
    }
    public void privateVariableMaker(String variableName)
    {
        pcm.append( PRIVATE_VARIABLE_MAKER + DOUBLE_CURLY_BRACES_OPEN + variableName.toLowerCase() + DOUBLE_CURLY_BRACES_CLOSE + SEMI_COLON + NEXT_LINE );
    }
    public void privateObjectMakerVariable(String variableName)
    {
        String declaration = variableName.substring(0, 1).toUpperCase() + variableName.substring(1).toLowerCase();
                   pcm.append( PRIVATE_OBJECT_VARIABLE_MAKER + ARRAY_OPEN + declaration + ARRAY_CLOSE + ONE_SPACE +
                           DOUBLE_CURLY_BRACES_OPEN + variableName.toLowerCase() +
                           DOUBLE_CURLY_BRACES_CLOSE +  SEMI_COLON + NEXT_LINE );
    }
    public void privateListMakerVariable(String variableName)
    {
        pcm.append( PRIVATE_OBJECT_VARIABLE_MAKER + LIST_VAR_OPEN + variableName.substring(0, 1).toUpperCase() +
                   variableName.substring(1) + LIST_VAR_CLOSE  + DOUBLE_CURLY_BRACES_OPEN + variableName +
                   DOUBLE_CURLY_BRACES_CLOSE + SEMI_COLON + NEXT_LINE );
    }




    //Prepare To Flying
    public void prepareToFly()
    {
        String MESSAGE = "************ Prepare To Fly ***************";
        System.out.println( ANSI_GREEN + MESSAGE );
    }

    //Landing Success
    public void landingSuccess()
    {
        String MESSAGE = "********** Landing Success *************";
        System.out.println( ANSI_GREEN + MESSAGE );
    }



    public String firstLetterCapital(String value)
    {
        // second substring contains remaining letters
        String firstLetter = value.substring(0, 1);
        String remainingLetters = value.substring(1, value.length());
        // change the first letter to uppercase
        firstLetter = firstLetter.toUpperCase();
        // join the two substrings
        return firstLetter + remainingLetters;
    }

}

