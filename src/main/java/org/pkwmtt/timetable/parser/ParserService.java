package org.pkwmtt.timetable.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pkwmtt.timetable.dto.DayOfWeekDTO;
import org.pkwmtt.timetable.dto.SubjectDTO;
import org.pkwmtt.timetable.dto.TimeTableDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ParserService {

    /**
     * Alters html code for it to fit parsing process
     *
     * @param html webpage content
     * @return altered html code
     */
    public String cleanHtml(String html) {
        return html.replaceAll("<br>", " ")
            .replaceAll(Pattern.quote("</span>-(N"), "-(N</span>")
            .replaceAll(Pattern.quote("</span>-(P"), "-(P</span>")
            .replaceAll(Pattern.quote("</span>-(p"), "-(p</span>")
            .replaceAll(Pattern.quote("</span>-(n"), "-(n</span>")
            .replaceAll(Pattern.quote("<span style=\"font-size:85%\">"), "")
            .replaceAll(Pattern.quote("<a"), "<span")
            .replaceAll(Pattern.quote("</a>"), "</span>");
    }

    /**
     * Extrude hours list from webpage
     *
     * @param html subpage of any general group
     * @return List of hours: <i>List of Strings</i>
     */
    public List<String> getHours(String html) {
        //Parse html code to Document object (allows to query elements)
        Document document = Jsoup.parse(html);

        List<String> hours = new ArrayList<>();

        for (Element item : document.select("td.g"))
            hours.add(item.text());

        return hours;
    }

    /**
     * Parse webpage html to map of general groups containing name of a group and link
     * to subpage with its timetable
     *
     * @param html .../list containing list of general groups
     * @return map of general groups in format [GroupName: URL]
     */
    public Map<String, String> parseGeneralGroupsHtmlToList(String html) {
        Document document = Jsoup.parse(html);

        Map<String, String> generalGroups = new HashMap<>();

        for (Element item : document.select("#oddzialy .el a"))
            generalGroups.put(item.text(), item.attr("href"));
        return generalGroups;
    }

    public List<DayOfWeekDTO> parse(String html) {
        List<DayOfWeekDTO> days = new ArrayList<>();

        Document document = Jsoup.parse(cleanHtml(html.replaceAll("&nbsp;", "")));
        Elements table = document.select("table");
        Elements rows = table.select("table tbody tr td table tbody tr");

        //Get first row containing headers
        Elements headers = rows.getFirst().select("th");

        //Delete first row
        rows.removeFirst();

        //Get name of each day
        for (int i = 2; i < headers.size(); i++)
            days.add(new DayOfWeekDTO(headers.get(i).text()));

        //Go every row
        for (int rowId = 0; rowId < rows.size(); rowId++) {
            Element row = rows.get(rowId);
            Elements cell = row.select("td.l");

            //Go every cell in a row
            for (int columnId = 0; columnId < cell.size(); columnId++) {
                Elements items = cell.get(columnId).select("span");

                //Delete professor initials and '#' code name
                items.removeIf(ec -> ec.text().contains("#") || ec.text().length() == 2);

                //Go every item in column
                for (int i = 0; i < items.size() - 1; i += 2) {
                    String name = items.get(i).text();
                    String classroom = items.get(i + 1).text();

                    SubjectDTO subject = SubjectDTO
                        .builder()
                        .name(name)
                        .classroom(classroom)
                        .rowId(rowId)
                        .build();

                    if (isNameOdd(name))
                        days.get(columnId).addToOdd(subject);
                    else if (isNameEven(name))
                        days.get(columnId).addToEven(subject);

                }
            }
        }

        return days;
    }

    private boolean isNameOdd(String name) {
        return !name.contains("(P") && !name.contains("-(p");
    }

    private boolean isNameEven(String name) {
        return !name.contains("(N") && !name.contains("-(n");
    }
}