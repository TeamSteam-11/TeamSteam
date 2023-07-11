
const character = document.querySelector(".character");
const game = document.querySelector(".game");
const startButton = document.querySelector(".start-button");

// 게임 창에 표시할 메시지 엘리먼트 생성
const messageElement = document.createElement("div");
messageElement.classList.add("message");
game.appendChild(messageElement);

// 메세지
function showNotification(message) {
    const notification = document.getElementById('notification');
    notification.innerText = message;
    notification.style.opacity = 0.8;

    setTimeout(() => {
        notification.style.opacity = 0;
    }, 3000);
}
// 변수 추가
let timerInterval;

// 타이머 시작 함수
function startTimer() {
    const timerElement = document.querySelector(".timer");
    let count = 0;

    timerInterval = setInterval(function () {
        count += 1;
        timerElement.textContent = count;
    }, 10);
}
// 브라우저 보일시, 안 보일시
let gamePaused = false;
document.addEventListener('visibilitychange', () => {
    if (document.hidden) {
        gamePaused = true;
    } else {
        gamePaused = false;
    }
});
// 화살 생성 함수
function createArrow() {
    if (gamePaused) {
        return;
    }
    const arrow = document.createElement("div");
    let randomNumber = Math.floor(Math.random() * 4) + 1;
    // document.querySelector('.img_box').classList.add('bg0' + randomNumber);
    arrow.classList.add("arrow");
    arrow.classList.add('bg0' + randomNumber);
    const direction = Math.floor(Math.random() * 4);

    // 화살에 direction 속성 추가
    arrow.setAttribute('data-direction', direction);

    switch (direction) {
        case 0: // 상단에서 생성
            arrow.style.top = "0";
            arrow.style.left = `${Math.floor(Math.random() * (game.clientWidth - arrow.clientWidth))}px`;
            break;
        case 1: // 우측에서 생성
            arrow.style.top = `${Math.floor(Math.random() * (game.clientHeight - arrow.clientHeight))}px`;
            arrow.style.right = "0";
            break;
        case 2: // 하단에서 생성
            arrow.style.bottom = "0";
            arrow.style.left = `${Math.floor(Math.random() * (game.clientWidth - arrow.clientWidth))}px`;
            break;
        case 3: // 좌측에서 생성
            arrow.style.top = `${Math.floor(Math.random() * (game.clientHeight - arrow.clientHeight))}px`;
            arrow.style.left = "0";
            break;
    }

    // 화살을 게임 창에 추가합니다.
    game.appendChild(arrow);

    // 화살의 방향 벡터를 설정합니다. (생성된 위치를 고려) 캐릭터로 날라가는
    const timerElement = parseInt(document.querySelector(".timer").textContent)
    let probabilityTowardsCharacter;
    if (timerElement < 2000) {
        probabilityTowardsCharacter = 0.5;
    } else if (2000 <= timerElement && timerElement < 4000) {
        probabilityTowardsCharacter = 0.8;
        if (2000 <= timerElement && timerElement < 2100) {
            showNotification("캐릭터에게 몰려오는 화살의 확률이 증가했습니다.");
        }
    } else if (4000 <= timerElement) {
        probabilityTowardsCharacter = 1;
        if (4000 <= timerElement && timerElement < 4100) {
            showNotification("캐릭터에게 몰려오는 화살의 확률이 최대로 증가했습니다.");
        }
    }
    if (5000 <= timerElement && timerElement <= 5100) {
        clearInterval(arrowCreationInterval);
        arrowCreationInterval = setInterval(createArrow, 200);
        showNotification("화살이 2배로 증가했습니다..");
    }

    if (Math.random() < probabilityTowardsCharacter) {
        // 화살이 캐릭터를 향하는 방향으로 발사됩니다.
        const characterPosition = character.getBoundingClientRect();
        const arrowPosition = arrow.getBoundingClientRect();

        arrow.directionX = (characterPosition.left + character.clientWidth / 2) - (arrowPosition.left + arrow.clientWidth / 2);
        arrow.directionY = (characterPosition.top + character.clientHeight / 2) - (arrowPosition.top + arrow.clientHeight / 2);
    } else {
        // 화살이 랜덤한 방향으로 발사됩니다.
        switch (direction) {
            case 0: // 상단에서 생성
                arrow.directionX = Math.random() * 2 - 1;
                arrow.directionY = Math.random();
                break;
            case 1: // 우측에서 생성
                arrow.directionX = -Math.random();
                arrow.directionY = Math.random() * 2 - 1;
                break;
            case 2: // 하단에서 생성
                arrow.directionX = Math.random() * 2 - 1;
                arrow.directionY = -Math.random();
                break;
            case 3: // 좌측에서 생성
                arrow.directionX = Math.random();
                arrow.directionY = Math.random() * 2 - 1;
                break;
        }
    }


    // 방향 벡터의 길이를 구합니다.
    const length = Math.sqrt(arrow.directionX * arrow.directionX + arrow.directionY * arrow.directionY);

    // 방향 벡터를 정규화합니다.
    arrow.directionX /= length;
    arrow.directionY /= length;



    return arrow;
}

// 화살 움직임 함수
function moveArrows() {
    const arrows = document.querySelectorAll(".arrow");
    const timerElement = parseInt(document.querySelector(".timer").textContent)

    arrows.forEach((arrow) => {

        const arrowPosition = arrow.getBoundingClientRect();
        const characterPosition = character.getBoundingClientRect();
        // 화살이 움직일 속도를 설정합니다.
        let speed;
        if (timerElement < 1000) { //임시
            speed = 5;
        } else if (1000 <= timerElement && timerElement < 3000) {
            speed = 7;
            if (1000 <= timerElement && timerElement < 1100) {
                showNotification("화살의 스피드가 증가했습니다.");
            }
        } else if (3000 <= timerElement) {
            speed = 10;
            if (3000 <= timerElement && timerElement < 3100) {
                showNotification("화살의 스피드가 최대로 증가했습니다.");
            }
        }
        // 대각선 방향으로 화살을 이동시킵니다.
        arrow.style.left = `${arrow.offsetLeft + speed * arrow.directionX}px`;
        arrow.style.top = `${arrow.offsetTop + speed * arrow.directionY}px`;

        // 캐릭터와 화살이 충돌했는지 확인
        const arrowCenterX = arrowPosition.left + arrowPosition.width / 2;
        const arrowCenterY = arrowPosition.top + arrowPosition.height / 2;
        const characterCenterX = characterPosition.left + characterPosition.width / 2;
        const characterCenterY = characterPosition.top + characterPosition.height / 2;

        const distanceBetweenCenters = Math.sqrt(Math.pow(arrowCenterX - characterCenterX, 2) + Math.pow(arrowCenterY - characterCenterY, 2));
        const sumOfRadii = (arrowPosition.width / 2) * 0.8 + (characterPosition.width / 2) * 0.8;

        if (distanceBetweenCenters < sumOfRadii) {
            // 게임 종료
            console.log("게임 종료!!");
            gameOver();
        }

        // 화살이 게임 창 밖으로 나갔을 경우 제거
        if (
            arrow.offsetTop + arrow.offsetHeight / 2 > game.clientHeight ||
            arrow.offsetTop + arrow.offsetHeight / 2 < 0 ||
            arrow.offsetLeft + arrow.offsetWidth / 2 < 0 ||
            arrow.offsetLeft + arrow.offsetWidth / 2 > game.clientWidth
        ) {
            game.removeChild(arrow);
        }
    });

    // 다음 프레임에서 moveArrows 함수를 다시 실행합니다.
    requestAnimationFrame(moveArrows);
}

// 화살 생성 간격 설정
let arrowCreationInterval;


// 시작 버튼 클릭 이벤트
startButton.addEventListener("click", function(event) {
    // 비활성화 클래스 제거
    game.classList.remove("disabled");
    // 시작 버튼 숨기기
    startButton.style.display = "none";
    // 타이머 시작
    startTimer();
    // 화살 움직임 시작
    arrowInterval = requestAnimationFrame(moveArrows); // 50ms마다 화살 움직임 업데이트
    // 화살 생성 시작
    arrowCreationInterval = setInterval(createArrow, 400); // 500ms마다 화살 생성

    // 게임 창 크기 조정
    game.style.width = "100%";
    game.style.height = "100%";
});

// 기본 우클릭 동작을 막기 위해 contextmenu 이벤트를 사용합니다.
game.addEventListener("contextmenu", function(event) {
    event.preventDefault();
});

// 캐릭터 방향 애니메이션
const gameScreen = document.querySelector('.game-container');
gameScreen.addEventListener("mousemove", function(event) {
    const character = document.querySelector(".character");
    const mouseX = event.clientX;
    const mouseY = event.clientY;
    const characterX = character.getBoundingClientRect().left + character.clientWidth / 2;
    const characterY = character.getBoundingClientRect().top + character.clientHeight / 2;

    const radian = Math.atan2(mouseY - characterY, mouseX - characterX);
    const angle = radian * (180 / Math.PI);

    character.style.transform = "rotate(" + angle + "deg)";
});

// mousedown 이벤트를 사용하여 마우스 오른쪽 버튼을 누르는 순간 캐릭터를 이동시킵니다.
game.addEventListener("mousedown", function(event) {

    if (event.button === 2) {
        const oldPointer = document.querySelector('.pointer')
        const pointer = document.createElement('div')
        pointer.style.left = `${event.clientX}px`
        pointer.style.top = `${event.clientY}px`
        pointer.classList.add('pointer')
        oldPointer ?
            document.body.replaceChild(pointer, oldPointer) :
            document.body.appendChild(pointer)
        // 타겟 지점 (캐릭터의 중앙을 고려하여 수정)
        const targetX = event.clientX - game.offsetLeft - character.offsetWidth / 2;
        const targetY = event.clientY - game.offsetTop - character.offsetHeight / 2;
        // 현재 위치
        const currentX = parseInt(getComputedStyle(character).left);
        const currentY = parseInt(getComputedStyle(character).top);

        // 좌표간 거리
        const distanceX = targetX - currentX;
        const distanceY = targetY - currentY;

        // 이동 거리
        const distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        const speed = 400; // 이동 속도 (픽셀/초)
        const time = distance / speed; // 이동 시간 (초)

        const leftLeg = document.querySelector(".left-leg");
        const rightLeg = document.querySelector(".right-leg");
        const leftLegBack = document.querySelector(".left-leg-back");
        const rightLegBack = document.querySelector(".right-leg-back");

        // 다리 애니메이션 적용
        let isLeft = true; // 왼발부터 시작
        let interval = setInterval(() => {
            if (isLeft) {
                leftLeg.style.visibility = "visible";
                leftLegBack.style.visibility = "hidden";
                rightLeg.style.visibility = "hidden";
                rightLegBack.style.visibility = "visible";
                isLeft = false;
            } else {
                leftLeg.style.visibility = "hidden";
                leftLegBack.style.visibility = "visible";
                rightLeg.style.visibility = "visible";
                rightLegBack.style.visibility = "hidden";
                isLeft = true;
            }
        }, 100); // 0.2초마다 왼발, 오른발 번갈아가며 보이도록 설정

        character.style.transition = `left ${time}s linear, top ${time}s linear`;
        character.style.left = `${targetX}px`;
        character.style.top = `${targetY}px`;

        // 캐릭터 이동이 끝나면 다리 애니메이션 정지 및 다리 숨기기
        setTimeout(() => {
            clearInterval(interval);
            leftLeg.style.visibility = "hidden";
            leftLegBack.style.visibility = "hidden";
            rightLeg.style.visibility = "hidden";
            rightLegBack.style.visibility = "hidden";
        }, time * 1000);
    }
});

// 게임 오버 함수
function gameOver() {
    console.log("게임오버는 돼?")
    clearInterval(timerInterval);
    cancelAnimationFrame(arrowInterval);
    clearInterval(arrowCreationInterval);

    // 점수 애니메이션 수정
    const scoreElement = document.querySelector(".timer");
    const score = parseInt(scoreElement.innerText);
    if(currentUserId == null){
        changeStartButtonToRestart();
    }

    // typeof currentUserId !== "undefined" &&
    if (currentUserId != null) {
        const userId = currentUserId;

        const data = { score: score };

        console.log("header : ", header);
        console.log("token : ", token);

        fetch(`/minigame/${userId}/scoreSave`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [header]: token
            },
            body: JSON.stringify(data),
        })
            .then((response) => {
                if (response.ok) {
                    changeStartButtonToRestart();
                    console.log("점수가 저장되었습니다.");
                } else {
                    console.error("오류 발생");
                }
            })
            .catch((error) => {
                console.error("오류 발생", error);
            });
    }

    score.style.animation = "scoreAnimation 1s forwards";

    // 화살 속도 초기화
    resetArrowSpeed();
}
// 화살 속도 초기화 함수
function resetArrowSpeed() {
    arrows.forEach((arrow) => {
        arrow.style.transition = "none";
    });
}
// 게임 시작 버튼을 다시하기 버튼으로 변경하는 함수
function changeStartButtonToRestart() {
    // const startButton = document.querySelector(".start-button");
    startButton.textContent = "다시하기";
    startButton.style.display = "block";
    startButton.onclick = () => {
        // 캐릭터 위치 초기화
        const character = document.querySelector(".character");
        character.style.left = "50%";
        character.style.top = "50%";

        // 전체 화살 제거
        const arrows = document.querySelectorAll(".arrow");
        arrows.forEach((arrow) => {
            arrow.remove();
        });

        // 점수 초기화
        const score = document.querySelector(".timer");
        score.textContent = "0";
        score.style.animation = "scoreAnimationBefore 1s forwards";
    };
}

// 화면 축소시 캐릭터 중앙으로 강제이동
window.addEventListener('resize', function() {
    const gameContainer = document.querySelector('.game-container');
    const game = document.querySelector('.game');
    const character = document.querySelector('.character');

    // gameContainer와 game의 크기 조정
    gameContainer.style.width = window.innerWidth + 'px';
    gameContainer.style.height = window.innerHeight + 'px';
    game.style.width = gameContainer.clientWidth + 'px';
    game.style.height = gameContainer.clientHeight + 'px';

    // character의 위치 조정
    character.style.left = `${game.clientWidth / 2}px`;
    character.style.top = `${game.clientHeight / 2}px`;
});
