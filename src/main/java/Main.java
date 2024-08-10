import io.qameta.allure.Attachment;
import io.qameta.allure.Step;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Main {

    static Map<String, String> parameters = new HashMap() {
        {
            put("warehouseCode", "valueCode");
            put("warehouseName", "valueName");
            put("active", "true");
            put("updated_by", null);
            put("created_by", "null");
            put("legalEntityCode", "value");
        }
    };


    @Step("Подготовим sql запрос")
    private static String prepareSqlQuery(Map<String, String> parameters, String pathSqlQuery) throws Exception {
        //TODO необходимо реализовать метод, который будет возвращать подготовленный sql запрос.
        // Необходимо прочитать файл с sql запросом и подставить параметры.
        // При реализации метода учесть, что null и "null" должны корректно отображаться в результирующем запросе.


        String sql = Files.readString(Path.of(pathSqlQuery));
        while (sql.contains("${")) {
            String fullLine = sql.substring(sql.indexOf("${") - 1, sql.indexOf("}") + 2);
            System.out.println(fullLine);
            String v;
            String k;
            if (fullLine.contains(":-")) {
                 k = fullLine.substring(fullLine.indexOf("{") + 1, fullLine.indexOf(":-"));
                 v = fullLine.substring(fullLine.indexOf(":-") + 2, fullLine.indexOf("}"));
            }
            else {
                 k = fullLine.substring(fullLine.indexOf("{") + 1, fullLine.indexOf("}"));
                 v = "";
            }

            if (fullLine.contains("'${")) {
                if (v.contains("true") ||
                        v.contains("false") ||
                        v.contains("null") ||
                        String.valueOf(parameters.get(k)).equals("true") ||
                        String.valueOf(parameters.get(k)).equals("false") ||
                        String.valueOf(parameters.getOrDefault(k, "false")).equals("null")) {
                    fullLine = fullLine.substring(fullLine.indexOf("${") - 1, fullLine.indexOf("}") + 2);
                } else {
                    fullLine = fullLine.substring(fullLine.indexOf("${"), fullLine.indexOf("}") + 1);

                }
            } else {
                fullLine = fullLine.substring(fullLine.indexOf("${"), fullLine.indexOf("}") + 1);
            }
            if (parameters.containsKey(k)) {
                sql = sql.replace(fullLine, String.valueOf(parameters.get(k)));
            } else {
                if (v.isEmpty())
                {
                    throw new Exception("В параметре " + k + " " + pathSqlQuery + " отсутствует значение");
                }
                else {
                    sql = sql.replace(fullLine, v);
                }
            }

        }
        return sql;


    }


    /**
     * Выполним sql запрос из файла с подстановкой параметров
     *
     * @param parametersSqlQuery мапа с параметрами для подстановки в sql запрос
     * @param sqlQueryPath       путь к файлу с sql запросом
     * @throws java.sql.SQLException исключение, возникающее при ошибке выполнения sql запроса
     * @throws java.io.IOException   исключение, возникающее при ошибке чтения файла с sql запросом
     */
    @Attachment(value = "query", fileExtension = ".sql", type = "text/plain")
    @Step("Выполним sql запрос из файла с подстановкой параметров")
    public static void executeSqlQuery(java.util.Map<String, String> parametersSqlQuery, String
            sqlQueryPath) throws Exception {
        //В оригинальной реализации здесь находится запрос,
        //который выполняется для подготовки данных к бд, его реализовывать не обязательно

        System.out.println(prepareSqlQuery(parametersSqlQuery, sqlQueryPath));
    }

    public static void main(String[] args) throws Exception {
        executeSqlQuery(parameters, "src/main/resources/CreateWarehouse.sql");
    }
}