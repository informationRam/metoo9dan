// JavaScript 코드

// 더블 클릭 이벤트 핸들러
document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.membertable tbody').addEventListener('dblclick', function(event) {
     console.log('더블 클릭 이벤트가 발생.');
        const targetRow = event.target.closest('tr');
        if (targetRow) {
            // 해당 행에 있는 memberNo 값을 가져옵니다.
           const memberNo = targetRow.querySelector('.memberCheckbox').id;
            // 모달을 열고 데이터를 조회하는 함수를 호출합니다.
            openMemberDetailsModal(memberNo);
        }
    });
});

    // 모달 열기
    function openModal() {
      var myModal = new bootstrap.Modal(document.getElementById('memberDetailsModal'));
      myModal.show();
    }

let data;
        //모달 데이터 가져오기
        function openMemberDetailsModal(memberNo) {
            // 모달 내부의 입력 필드 초기화 (예: document.querySelector('#memberName').textContent = '';)
           document.querySelector('#memberMemo').textContent = '';
           document.querySelector('#memberName').textContent = '';
           document.querySelector('#memberBirth').value = '';
           document.querySelector('#memberTel').value = '';
           document.querySelector('#memberEmail').value = '';
           document.querySelector('#sidoDropdown').value = '';
           document.querySelector('#sigunguDropdown').value = '';

            // 모달 열 때 memberId를 저장
             document.querySelector('#editButton').setAttribute('data-memberno', memberNo);

            // Member 정보 서버에서 가져오기
            fetch(`/admin/members/${memberNo}`) // 적절한 API 엔드포인트 및 메서드를 사용하세요
                .then(response => response.json())
                .then(memberData  => {
                data = memberData;
                const userRole = data.role;
                    // 데이터를 모달에 채웁니다.
                    document.querySelector('#memberName').textContent = data.name;
                    document.querySelector('#memberMemo').value = data.memberMemo || '';

                    // 생년월일 설정
                    document.querySelector('#memberBirth').value = data.birth || '';
                        // data.birth가 유효한 날짜 문자열인 경우
                        console.log(data.birth); //2023-10-09T15:00:00.000+00:00 형식 변환 필요
                        if (data.birth) {
                                // data.birth에서 T 이후의 부분을 잘라내고 날짜만 가져옵니다
                                const datePart = data.birth.split('T')[0];

                                // 날짜를 JavaScript Date 객체로 변환합니다
                                const date = new Date(datePart);

                                // 이제 date를 사용하여 입력 필드에 날짜를 설정합니다
                                const year = date.getFullYear();
                                const month = String(date.getMonth() + 1).padStart(2, '0');
                                const day = String(date.getDate()).padStart(2, '0');
                                const formattedDate = `${year}-${month}-${day}`;
                                // 입력 필드에 날짜 설정
                                document.querySelector('#memberBirth').value = formattedDate;;
                        }

                        // Thymeleaf로 표현된 부분을 업데이트
                        var memberDetailsModalLabel = document.querySelector('#memberDetailsModalLabel');
                        memberDetailsModalLabel.textContent =   data.name + '님의 상세정보';

                        document.querySelector('#memberTel').value = data.tel;
                        document.querySelector('#memberEmail'  ).value = data.email;

                        // 멤버십 상태(유,무료) 가져오기
                        fetch(`/admin/members/${memberNo}/membershipStatus`)
                            .then(response => response.text())
                            .then(membershipStatus => {
                                document.querySelector('#membership').textContent = membershipStatus;
                            })
                            .catch(error => {
                                console.error('멤버십 상태 가져오기 실패:', error);
                            });

                    // Role이 Educator일 때만 학교 정보 및 주소 정보를 가져옵니다.
                  if (userRole === 'EDUCATOR') {
                        //교육자 정보 가져오기
                         fetch(`/admin/members/${memberNo}/educatorInfo`)
                               .then(response => response.json())
                               .then(educatorInfoData => {

                                    // Role이 Educator일 때만 학교 정보 및 주소 정보를 가져옵니다.
                                     document.querySelector('#memberSchoolName').value = educatorInfoData.schoolName;
                                    // 시도 드롭다운 업데이트
                                    populateSidoDropdown(educatorInfoData.sido);
                                     if (educatorInfoData.sido) {
                                        // 선택한 시도에 따라 시군구 드롭다운 업데이트
                                        updateSigunguDropdown(educatorInfoData.sido, educatorInfoData.sigungu);
                                        // Role이 Educator일 때만 해당 요소를 표시
                                       document.getElementById('schoolAndAddressRow').style.display = 'table-row';
                                     }
                               })
                        .catch(error => {
                        console.error('데이터 가져오기 실패:', error);
                        });
                  }else{
                      // Role이 Educator가 아닐 때 해당 요소를 숨김
                      document.getElementById('schoolAndAddressRow').style.display = 'none';
                  }
                        // 결제 정보를 가져오는 AJAX 요청 수행
                        fetch(`/admin/members/${memberNo}/payments`) // 결제 정보를 가져올 API 엔드포인트를 설정하세요
                            .then(response => response.json())
                            .then(paymentData => {
                              document.querySelector('#purchaseCount').textContent = paymentData.count;
                                // 모달 열기
                                openModal();
                            })
                            .catch(error => {
                                console.error('결제 정보 가져오기 실패:', error);
                            });
                })


            //모달 데이터 수정값 전송
            document.querySelector('#editButton').addEventListener('click', function() {

            // 유효성 검사 함수 호출
           if (validateForm()) {
               // 모달에서 사용자가 수정한 데이터 가져오기
                   const memberNo = document.querySelector('#editButton').getAttribute('data-memberno');
                   const updatedMemberData = {
                       birth: document.querySelector('#memberBirth').value,
                       tel: document.querySelector('#memberTel').value,
                       email: document.querySelector('#memberEmail').value,
                       memberMemo: document.querySelector('#memberMemo').value,
                   };

                   // 서버로 수정된 데이터 전송
                   fetch(`/admin/members/${memberNo}/updateMemberData`, {
                       method: 'POST',
                       headers: {'Content-Type': 'application/json',
                       },
                       body: JSON.stringify(updatedMemberData),
                   })
                       .then(response => {
                           if (response.ok) {
                             // Check the user's role
                               if (data && data.role     === 'EDUCATOR') {
                                   //MEMBER 데이터 업데이트 성공 & 교육자일 경우 EduInfo 업데이트 수행
                                    const updatedEducatorData = {
                                         schoolName : document.querySelector('#memberSchoolName').value,
                                         sido : document.querySelector('#sidoDropdown').value,
                                         sigungu : document.querySelector('#sigunguDropdown').value,
                                    };

                             // 서버로 EducatorInfo 데이터 업데이트 요청 보내기
                               fetch(`/admin/members/${memberNo}/updateEducatorData`, {
                                   method: 'POST',
                                   headers: {
                                       'Content-Type': 'application/json',
                                   },
                                   body: JSON.stringify(updatedEducatorData),
                               })

                                .then(educatorResponse => {
                                      if (educatorResponse.ok) {
                                          // EducatorInfo 데이터 업데이트 성공 시 모달 닫기
                                          alert("회원정보가 성공적으로 수정되었습니다");
                                          $('#memberDetailsModal').modal('hide');
                                      } else {
                                           alert("교육자 정보 수정 실패");
                                          console.error('EducatorInfo 데이터 수정 실패');
                                      }
                                  })
                                  .catch(error => {
                                   alert("교육자 정보 수정 실패");
                                      console.error('EducatorInfo 데이터 수정 실패:', error);
                                  });
                              } else {
                                    alert("회원정보가 성공적으로 수정되었습니다");
                                  // 교육자가 아니면 이미 성공, 모달 닫기
                                  $('#memberDetailsModal').modal('hide');

                              }
                          } else {
                              console.error('Member 데이터 수정 실패');
                               alert("회원정보 수정 실패");
                          }
                      })
                      .catch(error => {
                      console.error('Member 데이터 수정 실패:', error);
                           alert("회원정보 수정 실패");
                      });
           }
            });
    }





    // 시도와 시군구 데이터
    const koreaData = {
        "서울특별시": ["종로구", "중구", "용산구","성동구", "광진구", "동대문구", "중랑구", "성북구", "강북구", "도봉구", "노원구", "은평구", "서대문구", "마포구", "양천구", "강서구", "구로구", "금천구", "영등포구", "동작구", "관악구", "서초구", "강남구", "송파구", "강동구" ],
        "부산광역시": ["중구", "서구", "동구", "영도구", "부산진구", "동래구", "남구", "북구", "강서구", "해운대구", "사하구", "금정구", "연제구", "수영구", "사상구", "기장군" ],
        "인천광역시":[ "중구", "동구", "남구", "연수구", "남동구", "부평구", "계양구", "서구", "강화군", "옹진군"],
        "대구광역시":["중구", "동구", "서구", "남구", "북구", "수성구", "달서구", "달성군"],
        "광주광역시":[ "동구", "서구", "남구", "북구", "광산구"		],
        "대전광역시":["동구", "중구", "서구", "유성구", "대덕구"	],
        "울산광역시":["중구", "남구", "동구", "북구", "울주군" ],
        "세종특별자치시":[	],
        "경기도":[
                    "가평군", "고양시", "과천시", "광명시", "광주시", "구리시", "군포시", "김포시", "남양주시", "동두천시", "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시", "안양시", "양주시", "양평군", "여주시", "연천군", "오산시", "용인시", "의왕시", "의정부시", "이천시", "파주시", "평택시", "포천시", "하남시", "화성시"
                ],
        "강원도":[
                    "원주시", "춘천시", "강릉시", "동해시", "속초시", "삼척시", "홍천군", "태백시", "철원군", "횡성군", "평창군", "영월군", "정선군", "인제군", "고성군", "양양군", "화천군", "양구군"
                ],
        "충청북도":[
                    "청주시", "충주시", "제천시", "보은군", "옥천군", "영동군", "증평군", "진천군", "괴산군", "음성군", "단양군"
                ],
        "충청남도":[
                    "천안시", "공주시", "보령시", "아산시", "서산시", "논산시", "계룡시", "당진시", "금산군", "부여군", "서천군", "청양군", "홍성군", "예산군", "태안군"
                ],
        "경상북도":[
                    "포항시", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시", "상주시", "문경시", "경산시", "군위군", "의성군", "청송군", "영양군", "영덕군", "청도군", "고령군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군"
                ],
        "경상남도":[
                    "창원시", "김해시", "진주시", "양산시", "거제시", "통영시", "사천시", "밀양시", "함안군", "거창군", "창녕군", "고성군", "하동군", "합천군", "남해군", "함양군", "산청군", "의령군"
                ],
        "전라북도":[
                    "전주시", "익산시", "군산시", "정읍시", "완주군", "김제시", "남원시", "고창군", "부안군", "임실군", "순창군", "진안군", "장수군", "무주군"
                ],
        "전라남도":[
                    "여수시", "순천시", "목포시", "광양시", "나주시", "무안군", "해남군", "고흥군", "화순군", "영암군", "영광군", "완도군", "담양군", "장성군", "보성군", "신안군", "장흥군", "강진군", "함평군", "진도군", "곡성군", "구례군"
                ],
        "제주특별자치도":[	"제주시", "서귀포시" ]
            };

    // 시도, 시군구 드롭다운
    const sidoDropdown = document.getElementById('sidoDropdown');
    const sigunguDropdown = document.getElementById('sigunguDropdown');

    // 시도 드롭다운 업데이트
    function populateSidoDropdown(selectedSido) {
        sidoDropdown.innerHTML = '';

        const optionAll = document.createElement('option');
        optionAll.value = ''; // 선택 안 함 옵션
        optionAll.textContent = '시도 선택';
        sidoDropdown.appendChild(optionAll);

        for (const sido in koreaData) {
            const option = document.createElement('option');
            option.value = sido;
            option.textContent = sido;
            sidoDropdown.appendChild(option);
        }

        if (selectedSido) {
            // 선택한 시도를 선택
            sidoDropdown.value = selectedSido;
            // 해당 시도에 속하는 시군구 드롭다운 업데이트
            updateSigunguDropdown(selectedSido);
        }
    }

    // 시군구 드롭다운 업데이트
    function updateSigunguDropdown(selectedSido, selectedSigungu) {
        const sigunguDropdown = document.getElementById('sigunguDropdown');
        sigunguDropdown.innerHTML = '';

        const optionAll = document.createElement('option');
        optionAll.value = ''; // 선택 안 함 옵션
        optionAll.textContent = '시군구 선택';
        sigunguDropdown.appendChild(optionAll);

        if (selectedSido && koreaData[selectedSido]) {
            for (const sigungu of koreaData[selectedSido]) {
                const option = document.createElement('option');
                option.value = sigungu;
                option.textContent = sigungu;
                sigunguDropdown.appendChild(option);
            }
        }

        if (selectedSigungu) {
            // 선택한 시군구를 선택
            sigunguDropdown.value = selectedSigungu;
        }
    }

    //변경 이벤트 리스너
    sidoDropdown.addEventListener('change', function () {
        const selectedSido = sidoDropdown.value;
        // Update the sigunguDropdown based on the selected sido
        updateSigunguDropdown(selectedSido);
    });
