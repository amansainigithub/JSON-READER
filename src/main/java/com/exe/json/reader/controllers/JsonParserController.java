package com.exe.json.reader.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/jsonParser")
public class JsonParserController {

    public StringBuilder  prepareClassMaker = new StringBuilder();
    private String public_Class = "public Class ";
    private String curlBraceOpen = " { ";
    private String curlBraceClose = " } ";
    private String privateVariableMaker = "private String ";
    private String semiColon = " ; ";

    private String nextLine = " \n ";


      @PostMapping("/jp")
      public String JsonParserWithCom(@RequestBody String jsonP) {
            
            //System.out.println("Json Received Success \n " + jsonP);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(jsonP);
            JsonObject obj = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {

                // First Time We are COM AND COLLECT OBJECT DATA
                   if((obj.get(entry.getKey()).isJsonObject()))
                  {
                      System.out.println("=============******=============");
                      System.out.println("ROOT CLASS NAME :: " + entry.getKey());

                      //Append Data SB [String Builder]
                      // Root Class Name
                      prepareClassMaker.append(public_Class);
                      prepareClassMaker.append( entry.getKey() + curlBraceOpen + nextLine) ;
                      System.out.println("=============******=============");
                      isJsonObject(obj.get(entry.getKey()));
                      prepareClassMaker.append( nextLine + curlBraceClose + nextLine );

                  }else if(obj.get(entry.getKey()).isJsonArray())
                  {
                        System.out.println("JSON ARRAY :: " + entry.getKey());
                        isJsonArray(obj.get(entry.getKey()));
                  }
                  else if((obj.get(entry.getKey()).isJsonPrimitive()))
                  {
                  System.out.println("JSON PREMITIVE :: " + entry.getKey());
                  }
            }

            return prepareClassMaker.toString();
      }

      
         public void isJsonObject(JsonElement element) {
            System.out.println("== ISJSON-OBJECT IS START ==");
            JsonObject obj = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {

                  //inside DEEP ARR
                  if((obj.get(entry.getKey()).isJsonArray()))
                  {
                        System.out.println("Deep Array Runn");
                        isJsonArray(obj.get(entry.getKey()));
                  }
                  
                  //inside DEEP OBJ 
                  if((obj.get(entry.getKey()).isJsonObject()))
                  {
                      System.out.println("ROOT INNER OBJ : " + entry.getKey());
                        isJsonObject(obj.get(entry.getKey()));
                  }else {
                      //APPEND KEYS TO PRIVATE VAR
                      prepareClassMaker.append(privateVariableMaker + entry.getKey()  + semiColon + nextLine);
                      System.out.println("KEYS NAME OBJ : " + entry.getKey());
                  }
            }
            System.out.println("== isJsonObject is END ==");
      }
      
              
              public void isJsonArray(JsonElement element) {
            System.out.println("== isJson-Array is START ==");
            JsonArray jsonArray = element.getAsJsonArray();
             for(JsonElement ele : jsonArray) {
                   JsonObject obj = ele.getAsJsonObject();
                   Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
                        for (Map.Entry<String, JsonElement> entry : entries) {
                              System.out.println("Key : " + entry.getKey());
                              
                              //inside DEEP ARR
                              if((obj.get(entry.getKey()).isJsonArray()))
                              {
                                    isJsonArray(obj.get(entry.getKey()));
                              }
                              //inside DEEP OBJ
                              if((obj.get(entry.getKey()).isJsonObject()))
                              {
                                    isJsonObject(obj.get(entry.getKey()));
                              }
                        }  
              }
            
            System.out.println("== isJson-Array is END ==");
      }
}
