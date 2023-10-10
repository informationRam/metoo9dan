//  window.onload = function () {
      // 필수 약관 체크박스
      var agreement1 = document.getElementById("agreement1");
      var agreement2 = document.getElementById("agreement2");
      var agreement5 = document.getElementById("agreement5");

      // 본인 인증 폼 입력 필드
      var nameInput = document.querySelector(".authMember input[name='memName']");
      var toInput = document.querySelector(".authMember input[name='to']");
      var sendButton = document.querySelector(".authMember button");

      // 전체 동의 체크박스
      var checkAll = document.getElementById("checkAll");

      // 필수 약관 체크박스 이벤트 리스너
      agreement1.addEventListener("change", toggleAuthForm);
      agreement2.addEventListener("change", toggleAuthForm);
      agreement5.addEventListener("change", toggleAuthForm);

      // 입력 필드 초기화
      nameInput.disabled = true;
      toInput.disabled = true;
      sendButton.disabled = true;

      // 입력 필드 활성화/비활성화 함수
      function toggleAuthForm() {
          // 필수 약관에 모두 동의한 경우
          if (agreement1.checked && agreement2.checked && agreement5.checked) {
              nameInput.disabled = false;
              toInput.disabled = false;
              sendButton.disabled = false;
          } else {
              // 필수 약관 중 하나라도 동의하지 않은 경우
              nameInput.disabled = true;
              toInput.disabled = true;
              sendButton.disabled = true;
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


    //------------------인증문자 발송------------
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


     // 본인 인증 성공 시 호출되는 함수
     function onVerificationSuccess(userName, userPhone) {
         // 본인 인증 성공 메시지와 정보를 표시
         document.getElementById("verificationSuccess").style.display = "block";
         document.getElementById("userNameInput").textContent = userName;
         document.getElementById("userPhoneInput").textContent = userPhone;

         // 인증 코드 입력 화면 표시
         document.getElementById("verifyStep").style.display = "block";
         // 다음 단계로 이동
         nextPrev(1);
     }

// 인증 코드 확인 함수
function verifyCode() {
    // 사용자가 입력한 인증 코드 가져오기
    const verificationCode = document.getElementById("verificationCode").value;

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

                    // 세션에 저장된 이름과 휴대폰 번호 가져오기
                    const userName = response.userName;
                    const userPhone = response.userPhone;
                    console.log("세션저장 이름:",userName);
                    console.log("세션저장 Phone:",userPhone);

                    // 화면에 이름과 휴대폰 번호 출력
                    document.getElementById("verificationSuccess").style.display = "block";
                    document.getElementById("userNameInput").value = userName;
                    document.getElementById("userPhoneInput").value = userPhone;
                } else {
                    alert("인증에 실패했습니다.");
                }
            } else {
                alert("인증에 실패했습니다.");
            }
        }
    };

    xhr.send(JSON.stringify(requestData));
}



//     // "확인" 버튼 클릭 시 호출되는 함수
//     function verifyCode() {
//         const verificationCode = document.getElementById("verificationCode").value;
//
//         // AJAX 요청 생성: 입력한 인증 코드를 서버로 전송
//         const xhr = new XMLHttpRequest();
//         xhr.open('POST', '/member/verifyCode', true);
//         xhr.setRequestHeader("Content-Type", "application/json");
//         xhr.onreadystatechange = function() {
//             if (xhr.readyState === 4) {
//                 if (xhr.status === 200) {
//                     // 본인 인증 성공한 경우
//                     alert('본인 인증이 성공하였습니다.');
//                     // 인증 성공 시 다음 단계로 이동하도록 수정 가능
//                     nextPrev(1);
//                 } else {
//                     // 본인 인증 실패한 경우
//                     alert('본인 인증이 실패하였습니다.');
//                 }
//             }
//         };
//         xhr.send(JSON.stringify({ verificationCode: verificationCode }));
//     }





//} // window.onload 함수