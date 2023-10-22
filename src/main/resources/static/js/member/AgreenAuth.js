//  window.onload = function () {

      // 필수 약관 체크박스
      var agreement1 = document.getElementById("agreement1");
      var agreement2 = document.getElementById("agreement2");
      var agreement5 = document.getElementById("agreement5");

      // 본인 인증 폼 입력 필드
      var nameInput = document.querySelector(".authMember input[name='memName']");
      var toInput = document.querySelector(".authMember input[name='to']");
      var phoneVerificationCodeInput = document.querySelector(".authForm #phoneVerificationCode");
      // Add event listeners for the emailForm elements
      var emailNameInput = document.querySelector(".authForm #emailName");
      var valiEmailInput = document.querySelector(".authForm #valiEmail");
      var emailVerificationCodeInput = document.querySelector(".authForm #emailVerificationCode");
      var sendButton = document.querySelector(".authMember button");

      // 전체 동의 체크박스
      var checkAll = document.getElementById("checkAll");

      // 필수 약관 체크박스 이벤트 리스너
      agreement1.addEventListener("change", toggleAuthForm);
      agreement2.addEventListener("change", toggleAuthForm);
      agreement5.addEventListener("change", toggleAuthForm);

      // 입력 필드 초기화
//      nameInput.disabled = true;
//      toInput.disabled = true;
//      phoneVerificationCodeInput.disabled = true;
//      emailNameInput.disabled = true;
//      valiEmailInput.disabled = true;
//      emailVerificationCodeInput.disabled = true;
      sendButton.disabled = true;
     // sendEmailBtn.disabled = true;

//휴대폰 인증 or 이메일 인증 선택 시 해당 입력 필드 활성화/비활성화:
function toggleAuthMethod(selectedMethod) {
    if (selectedMethod === "phone") {
        document.getElementById("phoneForm").style.display = "block";
        document.getElementById("emailForm").style.display = "none";
    } else if (selectedMethod === "email") {
        document.getElementById("emailForm").style.display = "block";
        document.getElementById("phoneForm").style.display = "none";
    }
}

      // 입력 필드 활성화/비활성화 함수
      function toggleAuthForm() {
          // 필수 약관에 모두 동의한 경우 :
          if (agreement1.checked && agreement2.checked && agreement5.checked) {
                document.getElementById("phoneButton").disabled = false; // Enable phoneButton
                document.getElementById("emailButton").disabled = false; // Enable emailButton
//                nameInput.disabled = false;
//                toInput.disabled = false;
//                phoneVerificationCodeInput.disabled = false;
//                emailNameInput.disabled = false;
//                valiEmailInput.disabled = false;
//                emailVerificationCodeInput.disabled = false;
//                sendButton.disabled = false;

          } else {
              // 필수 약관 중 하나라도 동의하지 않은 경우
               document.getElementById("phoneButton").disabled = true;  // Disable phoneButton
               document.getElementById("emailButton").disabled = true;  // Disable emailButton
               document.getElementById("phoneForm").style.display = "none"; // Hide phone form
               document.getElementById("emailForm").style.display = "none"; // Hide email form
//                  nameInput.disabled = true;
//                  toInput.disabled = true;
//                  phoneVerificationCodeInput.disabled = true;
//                  emailNameInput.disabled = true;
//                  valiEmailInput.disabled = true;
//                  emailVerificationCodeInput.disabled = true;
//                  sendButton.disabled = true;
//                  document.getElementById("phoneButton").disabled = true;
//                  document.getElementById("emailButton").disabled = true;
//                  document.getElementById("phoneForm").style.display = "none";
//                  document.getElementById("emailForm").style.display = "none";
           }
      }

      // 전체 동의 체크박스 이벤트 리스너
      checkAll.addEventListener("change", function () {
          var checkboxes = document.querySelectorAll(".agreement input[type='checkbox']");
          for (var i = 0; i < checkboxes.length; i++) {
              checkboxes[i].checked = this.checked;
          }
          toggleAuthForm();
      });

      // 약관 동의 체크 상태 확인 함수
      function isAgreementChecked() {
          var checkboxes = document.querySelectorAll(".agreement input[type='checkbox']");
          var isAllChecked = true;
          for (var i = 0; i < checkboxes.length; i++) {
              if (checkboxes[i].getAttribute("required") && !checkboxes[i].checked) {
                  isAllChecked = false;
                  break;
              }
          }
          return isAllChecked;
      }

      // 각 약관 제목 클릭 시 상세 내용 펼치기/접기
      var agreements = document.querySelectorAll(".agreement");
      for (var i = 0; i < agreements.length; i++) {
          agreements[i].addEventListener("click", function () {
              var details = this.querySelector(".details");
              details.style.display = details.style.display === "block" ? "none" : "block";
          });
      }


/* -------------------유효성 검사 --------------------------------*/

        //유효성 검사
          function setErrorMessage(element, message) {
            element.style.color = "red";
            element.innerText = message;
        }

        function clearErrorMessage(element) {
            element.style.color = "";
            element.innerText = "";
        }
        //휴대폰인증 이름 유효성 검사
        function validateName(inputId, errorMessageId) {
            var nameInput = document.getElementById(inputId);
            var nameError = document.getElementById(errorMessageId);
            var namePatternEng = /^[A-Za-z]{2,}$/;
            var namePatternKor = /^[가-힣]{2,}$/;

            if (!namePatternEng.test(nameInput.value) && !namePatternKor.test(nameInput.value)) {
                setErrorMessage(nameError, "두 글자 이상의 한글/영어만 입력할 수 있으며, 동시 입력은 불가능합니다.");
                nameInput.style.borderColor = "red";
            } else if(namePatternEng.test(nameInput.value) && namePatternKor.test(nameInput.value)) {
                setErrorMessage(nameError, "영어와 한글 동시에 입력이 불가능합니다.");
                nameInput.style.borderColor = "red";
            } else {
                clearErrorMessage(nameError);
                nameInput.style.borderColor = "";
            }
            validateButtonStatus();
        }


        //폰인증 필드 검사(형식 + 중복)
        function validatePhoneNumberAndFormat(input) {
            var phoneInput = input;
            var phoneError = document.getElementById("phone-error-message");
            var phonePattern = /^\d{11}$/;
            // 입력된 값에서 숫자 이외의 문자 제거
            var phoneNumber = phoneInput.value.replace(/[^0-9]/g, '');
            // 입력 필드에 제거된 문자를 제외한 값 설정
            phoneInput.value = phoneNumber;

            if (!phonePattern.test(phoneNumber)) {
                setErrorMessage(phoneError, "휴대폰 형식이 올바르지 않습니다");
                phoneInput.style.borderColor = "red";
            }else{
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
                   validateButtonStatus(); // AJAX 요청이 완료된 이후에 호출
               })
                .catch(error => console.error('Error:', error));
                validateButtonStatus();
            }
        }

        //이메일인증 필드 검사 (형식 + 중복검사)
        function validateEmailAdressFormat(inputElement) {
            var emailError = document.getElementById("email-error-message");
            var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

            // 이메일 형식 확인
            if (!emailPattern.test(inputElement.value)) {
                setErrorMessage(emailError, "이메일 형식에 맞게 입력해주세요.");
                inputElement.style.borderColor = "red";
                return;
            } else {
                clearErrorMessage(emailError);
                inputElement.style.borderColor = "";
            }

            // 서버에 중복 확인 요청
            fetch('/member/checkEmailDuplication', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ valiEmail: inputElement.value })
            })
            .then(response => response.json())
            .then(data => {
                if (data.isDuplicate) {
                    setErrorMessage(emailError, "이미 가입한 계정이 있습니다.");
                    inputElement.style.borderColor = "red";
                } else {
                    clearErrorMessage(emailError);
                    inputElement.style.borderColor = "";
                }
                validateEmailBtn(); // AJAX 요청이 완료된 이후에 호출
            })
            .catch(error => {
                console.error("Error:", error);
                //validateEmailBtn(); // AJAX 요청이 완료된 이후에 호출
            });
        }

        //휴대폰인증 발송 막기
      function validateButtonStatus() {
          var nameInputValue = document.getElementById("memName").value;
          var phoneInputValue = document.getElementById("to").value;
          var nameError = document.getElementById("name-error-message");
          var phoneError = document.getElementById("phone-error-message");
          var sendButton = document.getElementById("phoneSendBtn");

          // 오류 메시지가 없고, 두 입력 필드에 값이 있는 경우에만 버튼을 활성화
          if (nameError.innerText === "" && phoneError.innerText === "" && nameInputValue.trim() !== "" && phoneInputValue.trim() !== "") {
              sendButton.disabled = false;
          } else {
              sendButton.disabled = true;
          }
      }
//        //이메일 발송버튼 막기
//          function validateEmailBtn() {
//              var nameInputValue = document.getElementById("memName").value;
//              var phoneInputValue = document.getElementById("to").value;
//              var nameError = document.getElementById("name-error-message");
//              var phoneError = document.getElementById("phone-error-message");
//              var sendButton = document.getElementById("phoneSendBtn");
//
//              // 오류 메시지가 없고, 두 입력 필드에 값이 있는 경우에만 버튼을 활성화
//              if (nameError.innerText === "" && phoneError.innerText === "" && nameInputValue.trim() !== "" && phoneInputValue.trim() !== "") {
//                  sendButton.disabled = false;
//              } else {
//                  sendButton.disabled = true;
//              }
//          }
      // 이메일인증 발송 버튼 막기
          function validateEmailBtn() {
              var emailName = document.getElementById('emailName').value;
              var valiEmail = document.getElementById('valiEmail').value;
              var nameError = document.getElementById('email-name-error-message');
              var emailError = document.getElementById('email-error-message');
              var sendButton = document.getElementById('sendEmailBtn');

              if (emailName.trim() !== "" && valiEmail.trim() !== "" && nameError.innerText === "" && emailError.innerText === "") {
                  sendButton.disabled = false;
              } else {
                  sendButton.disabled = true;
              }
          }
    //인증번호 유효성검사 (형식, 오류시 인증확인 버튼 비활)
    function validateVerificationCode(inputElement, errorMessageElement, verifyButtonElement) {
        var verificationCodePattern = /^\d{6}$/;

        if (!verificationCodePattern.test(inputElement.value)) {
            setErrorMessage(errorMessageElement, "인증번호 형식이 올바르지 않습니다");
            inputElement.style.borderColor = "red";
            verifyButtonElement.disabled = true; // 인증확인 비활성화
        } else {
            clearErrorMessage(errorMessageElement);
            inputElement.style.borderColor = "";
            verifyButtonElement.disabled = false; // 인증확인 활성화
        }
    }






/*--------------------------- 유효성 검사 끝 ------------------------------*/

var phoneVerified = false;
var emailVerified = false;

/*------------------본인인증 : 휴대폰  & 이메일 발송--------------*/
// AJAX 요청을 사용하여 서버에 SMS 발송 요청
    function sendVerificationCode() {
        // 이름과 휴대폰 번호 입력을 받는 부분
       const memName = document.getElementById("memName").value;
       const to = document.getElementById('to').value;

     // JavaScript 콘솔에 값 출력
        console.log("이름:", memName);
        console.log("Phone:", to);

    // API 요청을 위한 데이터 준비
        const requestData = {
            memName: memName,
            to: to
        };

     // 서버로 POST 요청 보내기
        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/api/sendSMS-code", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    const response = JSON.parse(xhr.responseText);
                    if (response.success) {
                        alert("인증 코드가 발송되었습니다.");
                        document.getElementById("verifyPhoneBtn").disabled = false;  // 여기서 인증확인 버튼 활성화
                    } else {
                        alert("인증 코드 발송에 실패했습니다.");
                    }
                } else {
                    alert("인증 코드 발송에 실패했습니다.");
                }
            }
        };

        xhr.send(JSON.stringify(requestData));
    }


//     // 본인 인증 성공 시 호출되는 함수
//     function onVerificationSuccess(userName, userPhone) {
//         // 본인 인증 성공 메시지와 정보를 표시
//         document.getElementById("verificationSuccess").style.display = "block";
//         document.getElementById("userNameInput").textContent = userName;
//         document.getElementById("userPhoneInput").textContent = userPhone;
//
//         // 인증 코드 입력 화면 표시
//         document.getElementById("verifyStep").style.display = "block";
//         // 다음 단계로 이동
//         nextPrev(1);
//     }

    // 인증 코드 확인 함수
    let phoneVerificationIsSuccess = false; //휴대폰 인증완료 초기값
    let emailVerificationIsSuccessful = false; // 이메일 인증완료 초기값

    function verifyCode() {
        // 사용자가 입력한 인증 코드 가져오기
        const verificationCode = document.getElementById("phoneVerificationCode").value;

        // API 요청을 위한 데이터 준비
        const requestData = {
            verificationCode: verificationCode
        };

        // 서버로 POST 요청 보내기
        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/api/verifyCode", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    const response = JSON.parse(xhr.responseText);
                    if (response.success) {
                         alert("본인 인증이 완료되었습니다.");
                          phoneVerificationIsSuccess = true;

                        // 세션에 저장된 이름과 휴대폰 번호 가져오기
                        const userName = response.userName;
                        const userPhone = response.userPhone;
                        console.log("세션저장 이름:",userName);
                        console.log("세션저장 Phone:",userPhone);

                         // 다음 단계로 이동하려면 다음Prev 함수를 호출합니다.
                        nextPrev(1);

                        // 화면에 이름과 휴대폰 번호 출력
                        //document.getElementById("verificationSuccess").style.display = "block";
                        //document.getElementById("verificationSuccess").style.display = "block";
                        document.getElementById("userNameInput").value = userName;
                        document.getElementById("userPhoneInput").value = userPhone;
                        // 입력 필드를 읽기 전용으로 설정
                        document.getElementById("userNameInput").readOnly = true;
                        document.getElementById("userPhoneInput").readOnly = true;
                    } else {
                        alert("인증을 완료해주세요.");
                    }
                } else {
                    alert("인증에 실패했습니다.");
                }
            }
        };

        xhr.send(JSON.stringify(requestData));
    }

    //이메일 인증번호 발송
    function sendEmailVerificationCode() {
        var email = document.getElementById("valiEmail").value;
        var emailName = document.getElementById("emailName").value;
          fetch("/api/sendEmailVerification", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    valiEmail: email  //valiEmail이 서버 전송 변수
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("인증 코드가 이메일로 전송되었습니다.");
                } else {
                    alert("이메일 전송 실패. 다시 시도해주세요.");
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            });
        }

    //이메일 본인인증 확인
    function verifyEmailCode() {
      var email = document.getElementById("valiEmail").value; //인증받을 이메일
      var name = document.getElementById("emailName").value;
      var inputCode = document.getElementById("emailVerificationCode").value; //사용자가 입력한 인증번호

        // 서버로 인증 코드 검증 요청
        fetch('/api/verifyEmailCode', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
             body: JSON.stringify({
                      valiEmail: email,
                      emailCode: inputCode
                  })
        })
        .then(response => {
                const contentType = response.headers.get("Content-Type");
                if (!contentType || !contentType.includes("application/json")) {
                    throw new Error("서버에서 유효하지 않은 형식의 응답이 왔습니다.");
                }

                if (response.status !== 200) {
                    // 성공적인 응답이 아니라면 JSON을 파싱하지 않고 에러를 던져줍니다.
                    throw new Error("인증번호를 다시 확인하세요");
                }
                return response.json();
            })
            .then(data => {
                alert("본인 인증이 완료되었습니다.")
                document.getElementById("userNameInput").value = name;
                document.getElementById("email").value = email;
                 // 입력 필드를 읽기 전용으로 설정
                document.getElementById("userNameInput").readOnly = true;
                document.getElementById("email").readOnly = true;
                emailVerificationIsSuccessful = true;
                nextPrev(1);
            })
            .catch(error => {
                console.error("Error:", error);
                alert("오류가 발생했습니다. 다시 시도해주세요.");
            });
    }