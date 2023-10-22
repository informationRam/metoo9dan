        // "회원가입하기" 링크 클릭 시 Register 탭을 활성화
        document.querySelector('#registerLink').addEventListener('click', function (e) {
            e.preventDefault();
            var registerTab = new bootstrap.Tab(document.querySelector('#tab-register'));
            registerTab.show();
        });

        //회원가입시 enter키 막기
        document.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                event.preventDefault(); // Enter 키의 기본 동작(폼 제출)을 중지합니다.
            }
        });



//        // 사용자가 'Next' 버튼을 클릭하면 호출되는 함수
//        function enableInputFields() {
//            document.getElementById("memberIdInput").removeAttribute("readonly");
//            document.getElementById("password").removeAttribute("readonly");
//            document.getElementById("confirmPassword").removeAttribute("readonly");
//            document.getElementById("birth").removeAttribute("readonly");
//            document.getElementById("emailInput").removeAttribute("readonly");
//        }

    //mulitStep-form 작동

                 var currentTab = 0; // 현재 탭을 첫 번째 탭(0)으로 설정합니다.
                 showTab(currentTab); // 현재 탭을 표시합니다.

                 function showTab(n) {
                   var x = document.getElementsByClassName("step");
                   x[n].style.display = "block"; // 현재 탭을 보여줍니다.

                   if (n == 0) {
                     document.getElementById("prevBtn").style.display = "none"; // 첫 번째 탭이면 "이전" 버튼을 숨깁니다.
                   } else {
                     document.getElementById("prevBtn").style.display = "inline"; // 첫 번째 탭이 아니면 "이전" 버튼을 표시합니다.
                   }

                   var nextBtn = document.getElementById("nextBtn");
                   if (n == x.length - 1) {
                     document.getElementById("nextBtn").innerHTML = "Submit"; // 마지막 탭이면 "다음" 버튼 텍스트를 "제출"로 변경합니다.
                     nextBtn.setAttribute("type", "submit"); // 버튼의 type을 "submit"으로 변경합니다.
                   } else {
                     document.getElementById("nextBtn").innerHTML = "Next"; // 마지막 탭이 아니면 "다음" 버튼 텍스트를 "다음"으로 변경합니다.
                     nextBtn.setAttribute("type", "button"); // 버튼의 type을 "button"으로 설정합니다.
                   }
                    // ... and run a function that displays the correct step indicator:
                   fixStepIndicator(n)
                 }

                 function nextPrev(n) {
                    var x = document.getElementsByClassName("step");

                    if (n == 1) {
                        if (!validateForm()) return false; // 현재 탭의 폼 유효성 검사가 실패하면 함수를 종료합니다.
                        if (currentTab === 1) { // 두 번째 스텝인 경우
                            if (!emailVerificationIsSuccessful && !phoneVerificationIsSuccess) { //둘다 인증되지 않으면 안넘어감, 하나라도 인증되면 넘어감
                                alert("본인 인증 단계를 완료하세요.");
                                return false; // 인증이 실패한 경우 다음으로 진행하지 않습니다.
                            }
                        }
                    }

                    x[currentTab].style.display = "none"; // 현재 탭을 숨깁니다.
                    currentTab = currentTab + n; // 다음 탭 또는 이전 탭으로 이동합니다.

                    if (currentTab === x.length - 1) {
                        var lastStepFormInputs = document.querySelectorAll("#lastStepForm input");
                        lastStepFormInputs.forEach(function (input) {
                            input.removeAttribute("disabled");
                        });
                    }

                    if (currentTab === x.length) {
                        document.getElementById("signUpForm").submit(); // 마지막 탭이면 폼을 제출합니다.
                    } else {
                        showTab(currentTab); // 다음 탭을 표시합니다.
                    }
                }


                //모든 step의 빈 필드 검사
                 function validateForm() {
                   var x, y, i, valid = true;
                   x = document.getElementsByClassName("step");
                   y = x[currentTab].getElementsByTagName("input");  //input 태그 검사

                   for (i = 0; i < y.length; i++) {
                      // phoneForm이 비활성화되었을 때, 해당 폼의 모든 input 필드를 생략
                         if (document.getElementById("phoneForm").style.display === "none" && y[i].closest("#phoneForm")) continue;
                      // emailForm이 비활성화되었을 때, 해당 폼의 모든 input 필드를 생략
                         if (document.getElementById("emailForm").style.display === "none" && y[i].closest("#emailForm")) continue;
                     //그 외는 검사
                     if (y[i].value == "") {
                       y[i].className += " invalid"; // 빈 필드에 "invalid" 클래스를 추가하여 사용자에게 알립니다.
                       valid = false;
                     }
                   }
                   if (valid) {
                     var stepIndicators = document.getElementsByClassName("stepIndicator");
                     stepIndicators[currentTab].className += " finish"; // 유효한 데이터가 입력되면 해당 단계를 완료로 표시합니다.
                   }
                   return valid;
                 }

                 function fixStepIndicator(n) {
                   var i, x = document.getElementsByClassName("stepIndicator");
                   for (i = 0; i < x.length; i++) {
                     x[i].className = x[i].className.replace(" active", ""); // 모든 단계에서 "active" 클래스를 제거합니다.
                   }
                   x[n].className += " active"; // 현재 단계에 "active" 클래스를 추가하여 활성화된 단계를 표시합니다.
                 }