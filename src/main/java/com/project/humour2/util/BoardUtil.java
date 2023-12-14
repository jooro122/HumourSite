package com.project.humour2.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class BoardUtil {

    //썸머노트 이미지(썸네일)관리를 위한 유틸 클래스
    public static List<String> summernoteImageUrls(String htmlContent) {
        List<String> imageUrls = new ArrayList<>();

        Document doc = Jsoup.parse(htmlContent);
        Elements imgElements = doc.select("img");

        for (Element imgElement : imgElements) {
            // src 속성 찾아 추출
            String imageUrl = imgElement.attr("src");
            imageUrls.add(imageUrl);
        }

        // summernoteImage가 하나도 없는 경우 기본값 설정
        if (imageUrls.isEmpty()) {
            imageUrls.add(null);
        }
        return imageUrls;
    }

    public static String getKoreanBoardName(String boardName) {
        if (boardName == null) {
            return ""; // 기본값 설정 또는 특정 동작 수행
        }
        switch (boardName) {
            case "sports":
                return "운동";
            case "reading":
                return "독서";
            case "hobby":
                return "취미";
            default:
                return "기타";
        }

    }

    public static String getCategoryText(Long categoryCode) {
        if (categoryCode == null) {
            return "기타"; //
        }

        // 카테고리 코드를 한글로 변환하는 로직을 추가
        switch (categoryCode.intValue()) {
                //운동 게시판
            case 101:
                return "축구";
            case 102:
                return "농구";
            case 103:
                return "야구";
                //독서 게시판
            case 201:
                return "소설";
            case 202:
                return "에세이";
            case 203:
                return "자기개발";
                //여행 게시판
            case 301:
                return "여행";
            case 302:
                return "음식";
            case 303:
                return "게임";
            default:
                return "기타";
        }
    }


}