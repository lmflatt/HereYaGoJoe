import com.sun.org.apache.xpath.internal.operations.Mod;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

public class PeopleWeb {
    static ArrayList<Person> people = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        Spark.init();

        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);
        String line = fileScanner.nextLine();

        while (fileScanner.hasNext()) {
            populateListFromFile(fileScanner.nextLine());
        }

        Spark.get(
                "/",
                ((request, response) -> {
                    String offset = request.queryParams("offset");
                    int offsetNum = 0;

                    Integer next1 = null;
                    Integer next2 = null;
                    Integer prev1 = null;
                    Integer prev2 = null;

                    Integer last = null;

                    if (offset != null) {
                        offsetNum = Integer.parseInt(offset);
                    }

                    Integer prev = null;
                    if (offsetNum >= 20) {
                        prev = (offsetNum - 20);
                        prev1 = prev/20 + 1;
                    }
                    Integer next = null;
                    if (offsetNum < people.size() - 20) {
                        next = (offsetNum + 20);
                        next1 = next/20 + 1;
                        last = people.size() - 20;
                    }

                    Integer muchLess = null;
                    if (prev != null && prev >= 20) {
                        muchLess = (prev - 20);
                        prev2 = muchLess/20 + 1;
                    }
                    Integer muchMore = null;
                    if (next != null && next < people.size() - 20) {
                        muchMore = (next + 20);
                        next2 = muchMore/20 + 1;
                    }

                    int current = offsetNum/20 + 1;


                    ArrayList<Person> people20 = new ArrayList<>();

                    for (int i = offsetNum; i < (offsetNum + 20); i++) {
                        people20.add(people.get(i));
                    }

                    HashMap m = new HashMap();
                    m.put("people", people20);
                    m.put("next1", next1);
                    m.put("prev1", prev1);
                    m.put("next2", next2);
                    m.put("prev2", prev2);
                    m.put("muchMore", muchMore);
                    m.put("muchLess", muchLess);
                    m.put("current", current);
                    m.put("prev", prev);
                    m.put("next", next);
                    m.put("last", last);
                    return new ModelAndView(m, "people.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                ((request, response) -> {
                    int id = Integer.parseInt(request.queryParams("id"));
                    int index = id - 1;
                    Person person = people.get(index);
                    HashMap m = new HashMap();

                    m.put("person", person);
                    return new ModelAndView(m, "person.html");
                }),
                new MustacheTemplateEngine()
        );
    }

    public static void populateListFromFile(String line) {
        String[] column = line.split(",");
        Person person = new Person(Integer.parseInt(column[0]), column[1], column[2], column[3], column[4], column[5]);

        people.add(person);
    }
}
