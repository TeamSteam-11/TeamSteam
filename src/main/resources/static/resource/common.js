function parseMsg(msg) {
    const [pureMsg, ttl] = msg.split(";ttl=");

    const currentJsUnixTimestamp = new Date().getTime();

    if (ttl && parseInt(ttl) + 5000 < currentJsUnixTimestamp) {
        return [pureMsg, false];
    }

    return [pureMsg, true];
}

// 어떠한 기능을 살짝 늦게(0.1초 미만)
function setTimeoutZero(callback) {
    setTimeout(callback);
}