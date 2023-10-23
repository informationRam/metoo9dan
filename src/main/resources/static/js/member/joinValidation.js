//회원가입 폼 마지막 step 유효성 검사

//아이디 폼
document.getElementById("memberIdInput").addEventListener("input", function() {
    const memberId = this.value;
      const regex = /^(?=.*[a-zA-Z])[a-zA-Z0-9]{1,20}$/; // 수정된 정규식
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
//아이디 중복 검사
document.getElementById("checkMemberId").addEventListener("click", function() {
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
                errorMsgElement.textContent = "사용가능한 아이디입니다";
                errorMsgElement.style.color = "green";
            }
        }
    })
    .catch(error => {
        console.error("Error:", error);
    });
});
//휴대폰 필드 중복검사
function validateUserPhoneNumber(input) {
    var phoneInput = input;
    var phoneError = document.getElementById("tel-duplication-error-message");
    var phonePattern = /^\d{11}$/;
    // 입력된 값에서 숫자 이외의 문자 제거
    var phoneNumber = phoneInput.value.replace(/[^0-9]/g, '');
    // 입력 필드에 제거된 문자를 제외한 값 설정
    phoneInput.value = phoneNumber;

    if (!phonePattern.test(phoneNumber)) {
        setErrorMessage(phoneError, "휴대폰 형식이 올바르지 않습니다");
        phoneInput.style.borderColor = "red";
    } else {
        // AJAX 요청을 통해 휴대폰 번호 중복 검사
        fetch('/member/checkPhoneNumberDuplication', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ tel: phoneNumber })
        })
            .then(response => response.json())
            .then(data => {
                if (data.isDuplicate) {
                    setErrorMessage(phoneError, "이미 가입한 계정이 있습니다.");
                    phoneInput.style.borderColor = "red";
                } else {
                    clearErrorMessage(phoneError);
                    phoneInput.style.borderColor = "";
                }
            })
            .catch(error => console.error('Error:', error));
    }
}

//비밀번호 입력검사
document.addEventListener("DOMContentLoaded", function() {
    // 비밀번호 유효성 검사 함수
    function isValidPassword(pwd) {
        const regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{6,}$/;
        return regex.test(pwd);
    }

    // 비밀번호 필드에 input 이벤트 리스너 추가
    document.getElementById("password").addEventListener("input", function() {
        const pwd = this.value;
        const errorMsgElement = document.querySelector(".pw1-error-message");

        if (!isValidPassword(pwd)) {
            errorMsgElement.textContent = "비밀번호는 최소 6자리 이상 숫자, 문자, 특수문자 각각 1개 이상 포함 되어야 합니다.";
        } else {
            errorMsgElement.textContent = "";
        }
    });

    // 비밀번호 확인 필드에 input 이벤트 리스너 추가
    document.getElementById("confirmPassword").addEventListener("input", function() {
        const pwd = document.getElementById("password").value;
        const confirmPwd = this.value;
        const errorMsgElement = document.querySelector(".pw2-pwMismatch-error-message");

        if (pwd !== confirmPwd) {
            errorMsgElement.textContent = "비밀번호가 일치하지 않습니다";
        } else {
            errorMsgElement.textContent = "";
        }
    });
});
//이메일 입력필드 검사
function validateEmailInput(emailInputElement) {
   var emailError = document.querySelector(".email-duplicate-error-message");
    var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

    // 이메일 형식 확인
    if (!emailPattern.test(emailInputElement.value)) {
        setErrorMessage(emailError, "이메일 형식에 맞게 입력해주세요.");
        emailInputElement.style.borderColor = "red";
        return;
    } else {
        clearErrorMessage(emailError);
        emailInputElement.style.borderColor = "";
    }

    // 서버에 중복 확인 요청
    fetch('/member/checkEmailDuplication', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: emailInputElement.value })
    })
    .then(response => response.json())
    .then(data => {
        if (data.isDuplicate) {
            setErrorMessage(emailError, "이미 가입한 계정이 있습니다.");
            emailInputElement.style.borderColor = "red";
        } else {
            clearErrorMessage(emailError);
            emailInputElement.style.borderColor = "";
        }
    })
    .catch(error => {
        console.error("Error:", error);
    });
}

