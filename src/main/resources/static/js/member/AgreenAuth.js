window.onload = function () {
    // 필수 약관 체크박스
    var agreement1 = document.getElementById("agreement1");
    var agreement2 = document.getElementById("agreement2");
    var agreement5 = document.getElementById("agreement5");

    // 본인 인증 폼 입력 필드
    var nameInput = document.querySelector(".authMember input[name='name']");
    var toInput = document.querySelector(".authMember input[name='to']");
    var sendButton = document.querySelector(".authMember button");

    // 전체 동의 체크박스
    var checkAll = document.getElementById("checkAll");

    // 필수 약관 체크박스 이벤트 리스너
    agreement1.addEventListener("change", toggleAuthForm);
    agreement2.addEventListener("change", toggleAuthForm);
    agreement5.addEventListener("change", toggleAuthForm);


    // 전체 동의 체크박스 이벤트 리스너
    checkAll.addEventListener("change", function () {
        var checkboxes = document.querySelectorAll(".agreement input[type='checkbox']");
        for (var i = 0; i < checkboxes.length; i++) {
            checkboxes[i].checked = this.checked;
        }
        toggleAuthForm();
    });

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
      agreements[i].addEventListener("click", function() {
          var details = this.querySelector(".details");
          details.style.display = details.style.display === "block" ? "none" : "block";
      });
    }

}; // window.onload 함수