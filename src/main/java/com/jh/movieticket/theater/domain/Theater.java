package com.jh.movieticket.theater.domain;

import com.jh.movieticket.config.BaseTimeEntity;
import com.jh.movieticket.theater.dto.TheaterServiceDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class Theater extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 상영관 이름

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seatList; // 좌석

    @Column
    private LocalDateTime deleteDate; // 삭제 날짜

    public void addSeat(Seat seat){

        seatList = seatList == null ? new ArrayList<>() : seatList;
        seatList.add(seat);
    }

    // Entity -> ServiceDto
    public TheaterServiceDto toServiceDto(){

        return TheaterServiceDto.builder()
            .id(id)
            .name(name)
            .seatCnt(seatList.size())
            .registerDate(getRegisterDate())
            .changeDate(getChangeDate())
            .deleteDate(deleteDate)
            .build();
    }
}
