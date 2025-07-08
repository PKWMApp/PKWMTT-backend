package org.pkwmtt.timetable.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
            .replaceAll(Pattern.quote("<span style='font-size:85%'>"), "")
            .replaceAll(Pattern.quote("<a"), "<span")
            .replaceAll(Pattern.quote("</a>"), "</span>");
    }

    /**
     * Extrude hours list from webpage
     *
     * @param html webpage content
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


//    export const getOdzialList =new
//
//    Promise((resolve, reject) =>
//
//    {
//  const list:
//    OddzialType[] = [];
//        axios
//            .get('http://podzial.mech.pk.edu.pl/stacjonarne/html/lista.html')
//            .then((res:any) =>{
//      const $ = cheerio.load(res.data);
//        $('#oddzialy .el a')
//            .toArray()
//            .forEach((e:any, i:number) =>
//        list.push({
//            name:$(e).text(),
//            link:$(e).attr('href'),
//          }),
//        );
//        resolve(list);
//    })
//    .catch((e:any) =>{
//        console.error(1, e);
//        reject([]);
//    });
//        return list;
//    });

    public String parse(String html) {
        StringBuilder newHtml = new StringBuilder(cleanHtml(html));
        getHours(newHtml.toString());

            return "";
    }

    public List<String> parseGeneralGroupsHtmlToList(String html){
        return new ArrayList<>();
    }

}
