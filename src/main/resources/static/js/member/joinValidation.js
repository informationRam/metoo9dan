//회원가입 폼 마지막 step 유효성 검사

//아이디 폼, 중복검사
document.getElementById("memberIdInput").addEventListener("input", function() {
    const memberId = this.value;
    const regex = /^[a-zA-Z0-9]{1,20}$/;
    const errorMsgElement = document.getElementById("memberId-duplication-error-message");

    // 유효성 검사
    if (!regex.test(memberId)) {
        this.setCustomValidity("20자 이하의 영문, 영문+숫자만 입력가능합니다");
        errorMsgElement.textContent = "20자 이하의 영문, 영문+숫자만 입력가능합니다";
        errorMsgElement.style.color = "red";
    } else {
        this.setCustomValidity("");
        errorMsgElement.textContent = ""; // Clear any existing error messages
    }
});

document.getElementById("chechMemberId").addEventListener("click", function() {
    const memberId = document.getElementById("memberIdInput").value;
    const errorMsgElement = document.getElementById("memberId-duplication-error-message");

    fetch('/member/checkMemberIdDuplication', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ memberId: memberId })
    })
    .then(response => response.json())
    .then(data => {
        if(data.isDuplicated) {
            errorMsgElement.textContent = "이미 사용중인 아이디입니다";
            errorMsgElement.style.color = "red";
        } else {
            if(document.getElementById("memberIdInput").checkValidity()) {
                errorMsgElement.textContent = "";
            }
        }
    })
    .catch(error => {
        console.error("Error:", error);
    });
});
