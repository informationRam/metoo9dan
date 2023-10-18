// 유효성 검사 함수

        function validateForm() {
               // 모든 필드의 유효성 검사를 통과하면 true 반환
               const isMemberBirthValid = validateMemberBirth();
               const isMemberTelValid = validateMemberTel();
               const isMemberEmailValid = validateMemberEmail();
               const isMemberMemoValid = validateMemberMemo();
               const isSidoDropdownValid = validateSidoDropdown();
               const isSigunguDropdownValid = validateSigunguDropdown();

               if (!isMemberBirthValid || !isMemberTelValid || !isMemberEmailValid || !isMemberMemoValid || !isSidoDropdownValid || !isSigunguDropdownValid) {
                   return false;
               }
               return true;
        }

                 // 입력 필드
                 const memberBirthInput = document.querySelector('#memberBirth');
                 const memberTelInput = document.querySelector('#memberTel');
                 const memberEmailInput = document.querySelector('#memberEmail');
                 const memberMemoInput = document.querySelector('#memberMemo');
//                 // 시도 드롭다운과 시군구 드롭다운
//                 const sidoDropdown = document.querySelector('#sidoDropdown');
//                 const sigunguDropdown = document.querySelector('#sigunguDropdown');

               // 오류 메시지 엘리먼트
                const errorMessages = {
                    memberBirth: document.querySelector('#memberBirthErrorMessage'),
                    memberTel: document.querySelector('#memberTelErrorMessage'),
                    memberEmail: document.querySelector('#memberEmailErrorMessage'),
                    memberMemo: document.querySelector('#memberMemoErrorMessage'),
                    sidoDropdown: document.querySelector('#sidoDropdownErrorMessage'),
                    sigunguDropdown: document.querySelector('#sigunguDropdownErrorMessage'),
                };

                 // 이벤트 리스너 추가
                 memberBirthInput.addEventListener('input', validateMemberBirth);
                 memberTelInput.addEventListener('input', validateMemberTel);
                 memberEmailInput.addEventListener('input', validateMemberEmail);
                 memberMemoInput.addEventListener('input', validateMemberMemo);

                 function validateMemberBirth() {
                     const value = memberBirthInput.value;
                     if (!value) {
                         showError(errorMessages.memberBirth, '생년월일을 입력해주세요.');
                          return false;
                     } else {
                         hideError(errorMessages.memberBirth);
                           return true;
                     }
                 }

                 function validateMemberTel() {
                     const phoneInput = memberTelInput;
                     const phoneError = errorMessages.memberTel; // 오류 메시지 엘리먼트
                     const phonePattern = /^\d{11}$/; // 11자리 숫자 형식

                     // 입력된 값에서 숫자 이외의 문자 제거
                     let phoneNumber = phoneInput.value.replace(/[^0-9]/g, '');

                     // 입력 필드에 변경된 값을 설정
                     phoneInput.value = phoneNumber;

                     if (!phonePattern.test(phoneNumber)) {
                         showError(phoneError, '휴대폰 형식이 올바르지 않습니다.');
                         return false; // 유효성 검사 실패
                     } else {
                         hideError(phoneError);
                         phoneInput.style.borderColor = '';
                          return true; // 유효성 검사 통과
                     }
                 }


                 function validateMemberEmail() {
                     const value = memberEmailInput.value;
                     const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i;

                     if (!value) {
                         showError(errorMessages.memberEmail, '이메일을 입력해주세요.');
                     } else if (!emailRegex.test(value)) {
                         showError(errorMessages.memberEmail, '이메일 형식이 올바르지 않습니다.');
                          return false;
                     } else {
                         hideError(errorMessages.memberEmail);
                         return true;
                     }
                 }

                 function validateMemberMemo() {
                     const value = memberMemoInput.value;
                     const maxMemoLength = 300;

                     if (value.length > maxMemoLength) {
                         showError(errorMessages.memberMemo, '300자 이내로 입력해주세요. (현재:'+ value.length +'/300)');
                         memberMemoInput.value = value.substring(0, maxMemoLength); // 300자 초과 입력 방지
                         return false;
                     } else {
                         hideError(errorMessages.memberMemo);
                         return true;
                     }
                 }

                // 시도 드롭다운 변경 감지
                 sidoDropdown.addEventListener('change', validateSidoDropdown);

                 // 시군구 드롭다운 변경 감지
                 sigunguDropdown.addEventListener('change', validateSigunguDropdown);

                 function validateSidoDropdown() {
                     const selectedSido = sidoDropdown.value;

                     if (selectedSido === '') {
                         showError(errorMessages.sidoDropdown, '시도를 선택해주세요.');
                          return false;
                     } else {
                         hideError(errorMessages.sidoDropdown);
                         return true;
                     }
                 }

                 function validateSigunguDropdown() {
                     const selectedSigungu = sigunguDropdown.value;

                     if (selectedSigungu === '') {
                         showError(errorMessages.sigunguDropdown, '시군구를 선택해주세요.');
                         return false;
                     } else {
                         hideError(errorMessages.sigunguDropdown);
                          return true;
                     }
                 }

                 function showError(element, message) {
                     element.textContent = message;
                     element.style.display = 'block';
                     element.style.borderColor  = 'red';
                     element.style.color = 'red';
                 }

                 function hideError(element) {
                     element.textContent = '';
                     element.style.display = 'none'; // 오류 메시지 숨김
                     element.style.borderColor = ''; // 원래의 borderColor로 되돌림
                     element.style.color = ''; // 원래의 텍스트 색상으로 되돌림
                 }

