package com.jh.movieticket.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 영화의 현재 상영 여부를 나타내는 enum 클래스
@Getter
@AllArgsConstructor
public enum ScreenType {
    NOW("NOW", "현재상영작"), // 현재상영작
    PREVIOUS("PREVIOUS", "지난상영작"), // 지난 상영작
    EXPECTED("EXPECTED", "상영예정작"); // 상영 예정작

    private final String name;
    private final String description;
}
