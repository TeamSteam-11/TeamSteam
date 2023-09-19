package com.ll.TeamSteam.domain.matching.entity;

import com.ll.TeamSteam.domain.chatMessage.dto.ChatMessageDto;
import com.ll.TeamSteam.domain.chatMessage.entity.ChatMessage;
import com.ll.TeamSteam.domain.matchingTag.entity.GenreTagType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateForm {

    @NotBlank(message = "제목은 필수항목입니다.")
    @Size(max= 50)
    private String title;
    @NotBlank(message = "내용 필수항목입니다.")
    @Column(columnDefinition = "TEXT")
    private String content;
    @NotNull(message = "장르는 필수항목입니다.")
    private GenreTagType genre;
    @NotNull(message = "게임태그는 필수항목입니다.")
    private Integer gameTagId;
    @Min(value = 2)
    @Max(value = 10, message = "모집인원의 범위를 벗어났습니다.")
    private Long capacity;
    private String gender;
    @NotNull(message = "시작시간은 필수항목입니다.")
    @Min(value = 0)
    @Max(value = 24)
    private Integer startTime;
    @NotNull(message = "끝나는 시간은 필수항목입니다.")
    @Min(value = 0)
    @Max(value = 24)
    private Integer endTime;
    private Integer selectedHours;

    private Boolean isMic;

    public CreateForm() {
        this.genre = GenreTagType.valueOf("삼인칭슈팅");
        this.gameTagId = 1;
//        this.capacity = 2L;
        this.gender = "성별무관";
//        this.startTime = 0;
        this.endTime = 0;
        this.selectedHours = 0;
    }

    public static CreateForm of(Matching matching) {

        CreateForm createForm = CreateForm.builder()
                .title(matching.getTitle())
                .content(matching.getContent())
                .genre(matching.getGenre())
                .gameTagId(matching.getGameTagId())
                .capacity(matching.getCapacity())
                .gender(matching.getGender())
                .startTime(matching.getStartTime())
                .endTime(matching.getEndTime())
                .isMic(matching.getIsMic())
                .build();

        return createForm;
    }
}
