let stompClient = null;
let fromMessageId = "000000000000000000000000";
let ChatMessageUl = null;
let currentDate = null; // 날짜 변수 추가

function getChatMessages() {
    console.log("dmId : " + dmId)
    fetch(`/dm/rooms/${dmId}/messages?fromMessageId=${fromMessageId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }})
        .then(response => {
            return response.text().then(text => {
                try {
                    return JSON.parse(text);
                } catch (e) {
                    console.error("Failed to parse JSON:", text);
                    throw e;
                }
            });
        })
        .then(body => {
            drawMessages(body);
            console.log(body);
        });
}

const lastChatMessages = {

};

let lastChatMessageUser = "";

function drawMessages(messages) {
    if (messages.length > 0) {
        fromMessageId = messages[messages.length - 1].message_id;
    }

    messages.forEach((message, index) => {
        const newItem = document.createElement("li");
        console.log(message);
        console.log("userId : " + userId);
        console.log("userName : " + userName);
        console.log("message.sender" + message.sender);

        if (message.senderId === userId) {
            newItem.classList.add("sender");
        } else {
            newItem.classList.add("receiver");
        }


        const createdAt = new Date(message.created_at);

        // 가공된 시간 표시 형식 (MM:SS)
        const hours = createdAt.getHours();
        const period = hours >= 12 ? "오후" : "오전";
        const displayHours = hours % 12 || 12; // 12시간 단위로 표시
        const minutes = String(createdAt.getMinutes()).padStart(2, '0');
        const formattedTime = `${period} ${displayHours}:${minutes}`;

        if ( lastChatMessages[message.senderId] === undefined ) {
            lastChatMessages[message.senderId] = {
                date: formattedTime,
                el: newItem
            };
        }

        if ( lastChatMessages[message.senderId].date == formattedTime ) {
            $(lastChatMessages[message.senderId].el).find('.message-time').remove();
        }

        if (message.senderId === userId) {
            newItem.innerHTML = `
                            <div><div class="chat chat-end">
                                 <div class="chat-bubble">${message.content}</div></div>
                                 <span class="message-time">${formattedTime}</span></div> `;
        } else {
            newItem.innerHTML = `
            <div class="chat chat-start">
                <div class="chat-image avatar">
                    <a href="/user/profile/${message.senderId}" class="flex items-center">
                        <div class="w-16 mr-1">
                            <img src="${message.senderAvatar}" alt="${message.senderUsername}'s avatar" class="rounded-lg"/>
                        </div>
                    </a>
                </div>
                <div class="chat-header mb-1">
                    <a href="/user/profile/${message.senderId}" class="flex items-center">
                        ${message.senderUsername}
                    </a>
                </div>
                <div class="chat-bubble">${message.content}</div>
                <div class="chat-footer message-time">
                    ${formattedTime}
                </div>
            </div>`;

            if ( lastChatMessageUser == message.senderId ) {
                $(newItem).find('.chat-header').remove();
                $(newItem).find('.chat-image').remove();
                $(newItem).find('.chat-bubble').addClass('no-image');
                $(newItem).find('.chat-footer').addClass('no-image');
            }
        }

        lastChatMessageUser = message.senderId;

        lastChatMessages[message.senderId].el = newItem;
        lastChatMessages[message.senderId].date = formattedTime;

        const messageDate = new Date(message.created_at).toLocaleDateString();

        if (currentDate !== messageDate) {
            const newDateItem = document.createElement("li");
            newDateItem.classList.add("center");
            newDateItem.textContent = messageDate;
            ChatMessageUl.appendChild(newDateItem);
            currentDate = messageDate;
        }

        ChatMessageUl.appendChild(newItem);
    });

    scrollToBottom();
}

function ChatWriteMessage(form) {

    form.content.value = form.content.value.trim();

    stompClient.send(`/app/dm/${dmId}/sendMessage`, {}, JSON.stringify({content: form.content.value}));

    form.content.value = '';
    form.content.focus();
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    const headers = {
        'X-CSRF-TOKEN': token,
    };

    stompClient.connect(headers, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe(`/topic/dm/${dmId}`, function (data) {
            getChatMessages();
        });
    });
}

document.addEventListener("DOMContentLoaded", function() {
    ChatMessageUl = document.querySelector('.chat__message-ul');
    getChatMessages();
    connect();
});

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
}

function scrollToBottom() {
    const chatMessages = document.querySelector('.msger-chat');
    chatMessages.scrollTop = chatMessages.scrollHeight;
}