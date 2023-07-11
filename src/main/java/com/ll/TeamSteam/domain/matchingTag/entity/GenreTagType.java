package com.ll.TeamSteam.domain.matchingTag.entity;

public enum GenreTagType {
    일인칭슈팅("일인칭슈팅"), 삼인칭슈팅("삼인칭슈팅"), 격투("격투"), 슛뎀업("슛뎀업"), 아케이드("아케이드"), 러너("러너"), 핵앤슬래시("핵앤슬래시"), JRPG("JRPG"), 로그라이크("로그라이크"),
    액션RPG("액션RPG"), 어드벤처RPG("어드벤처RPG"), 전략RPG("전략RPG"), 현대RPG("현대RPG"), 파티("파티"), 군사("군사"), 대전략("군사"), 도시("도시"), 실시간전략("실시간전략"), 카드("카드"),
    타워디펜스("타워디펜스"), 턴제전략("턴제전략"), 매트로베니아("매트로베니아"), 비주얼노벨("비주얼노벨"), 캐주얼("캐주얼"), 퍼즐("퍼즐"), 퐁부한스토리("퐁부한스토리"), 히든오브젝트("히든오브젝트"),
    건설("건설"), 농업("농업"), 샌드박스("샌드박스"), 생활("생활"), 연애("연애"), 우주("우주"), 취미("취미"), 스포츠("스포츠"), 낚시("낚시"), 레이싱("레이싱"), 스포츠시뮬레이션("스포츠시뮬레이션");

    private final String genre;

    GenreTagType(String genre) {
        this.genre = genre;
    }

    public static GenreTagType ofCode(String genreTagType) {
        for (GenreTagType gt : GenreTagType.values()) {
            if (gt.genre.equals(genreTagType)) {
                return gt;
            }
        }

        return null;
    }
}
