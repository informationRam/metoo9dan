//검색조건 이벤트 , 체크박스 회원 삭제

//체크박스 선택 이벤트

     let memberCheckboxes; // 이 부분을 상단에 추가
     document.addEventListener("DOMContentLoaded", attachCheckboxEventListeners);



            function attachCheckboxEventListeners() {
             // 전체 선택 체크박스 클릭 시 모든 체크박스 선택/해제
                     const selectAllCheckbox = document.getElementById("selectAllCheckbox");
                     memberCheckboxes = document.querySelectorAll(".memberCheckbox");

                 selectAllCheckbox.addEventListener("change", function() {
                     memberCheckboxes.forEㅣach(function(checkbox) {
                         checkbox.checked = selectAllCheckbox.checked;
                     });
                 });

                 // 개별 체크박스 클릭 시 전체 선택 체크박스 업데이트
                 memberCheckboxes.forEach(function(checkbox) {
                     checkbox.addEventListener("change", function() {
                         const allChecked = [...memberCheckboxes].every(checkbox => checkbox.checked);
                         selectAllCheckbox.checked = allChecked;
                     });
                 });
             }

         // 삭제 버튼 클릭 이벤트
         const deleteButton = document.getElementById("deleteMembersButton");
         deleteButton.addEventListener("click", function() {
             // 선택한 회원의 memberNo 값을 추적
             const selectedMembers = [];
             memberCheckboxes.forEach(function(checkbox) {
                 if (checkbox.checked) {
                     const memberNo = checkbox.id; // 체크박스의 id 속성으로 memberNo 값을 가져옵니다.
                     selectedMembers.push(memberNo);
                 }
             });

             if (selectedMembers.length === 0) {
                 alert("선택한 회원이 없습니다.");
             } else {
                 // 삭제 여부를 묻는 경고창 표시
                 const confirmation = confirm("선택한 회원을 삭제하시겠습니까?");
                 if (confirmation) {
                   // 서버로 선택한 회원 삭제 요청 보내기
                     deleteSelectedMembers(selectedMembers); // 예시 함수 이름, 실제로 구현해야 함
                 }
             }
         });


        // 선택한 회원 삭제 함수 예시 (서버에 삭제 요청을 보내도록 구현)
        function deleteSelectedMembers(selectedMembers) {
            // selectedMembers 배열을 서버에 전송하여 선택한 회원 삭제 요청을 보냅니다.
             console.log('선택한 회원 목록:', selectedMembers); // 확인용 로그
             // 서버로 삭제 요청을 보낼 URL 및 데이터를 설정
                const deleteUrl = '/admin/deleteMembers'; // 삭제 요청을 처리할 서버의 엔드포인트 URL
                const requestData = {
                    memberNos: selectedMembers // 선택한 회원들의 memberNo 배열
                };
            // 서버에서 요청 처리 후, 페이지를 갱신하거나 삭제된 회원 목록을 업데이트합니다.
              // 서버에 POST 요청으로 데이터 전송
                fetch(deleteUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(requestData)
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.text(); //서버에서 보낸 성패여부 응답 받기
                })
                .then(data => {
                    console.log('삭제 성공', data);
                    // 삭제가 성공했을 때 알림창 띄우기
                    window.alert('삭제가 완료되었습니다.');
                  // 알림창을 닫으면 페이지를 다시 로드
                    window.location.reload();

                })
                .catch(error => {
                    console.error('삭제 실패:', error);
                });
        }
   // })


//검색버튼 이벤트
        document.getElementById('searchBtn').addEventListener('click', function(event){
            event.preventDefault(); // 기본 폼 제출 동작을 중지 :submit 동작 눌려도 새로실 실행안함(submit은함)
            fetchSearchResults(); // 검색 결과를 가져옵니다.
        });

        //검색 조건 객체 설정
        function getSearchParams() {
            return {
                startDate: document.getElementById('startDate').value,
                endDate: document.getElementById('endDate').value,
                memberType: document.getElementById('memberType').value,
                membershipStatus: document.getElementById('membershipStatus').value,
                searchCriteria: document.getElementById('searchCriteria').value,
                searchKeyword: document.getElementById('searchKeyword').value,
                page: 1, // 기본 페이지는 1로 설정
                size: 15 // 페이지당 출력될 회원 수
            };
        }

//     //검색없어도 페이지 로드시 페이지네이션 생성
//       window.addEventListener('DOMContentLoaded', (event) => {
//                fetchSearchResults();
//            });
        //AJAX 검색결과 가져오기
    /*  content: 실제 데이터(회원 목록)를 포함하는 배열
        totalElements: 전체 데이터 수
        totalPages: 전체 페이지 수
        number: 현재 페이지 번호 (0부터 시작)*/
         function fetchSearchResults(pageNumber=1) {
             let params = getSearchParams();
             params.page = pageNumber; //받아온 페이지 번호 사용
             let queryString = new URLSearchParams(params).toString();
             console.log(params); // 검색조건 확인

             fetch(`/admin/search?${queryString}`)
                 .then(response => {
                     if (!response.ok) {
                         throw new Error('Network response was not ok');
                     }
                     return response.json();
                 })
                 .then(data => {
                     console.log(data); // 여기서 응답 데이터 확인
                    if (data.content.length === 0) {
                        // 검색 결과가 없는 경우 메시지를 표시
                        displayNoResultsMessage();
                    } else {
                        // 검색 결과가 있는 경우 데이터 표시 및 페이지네이션 생성
                        displayResults(data.content);
                        generatePagination(data.totalPages,pageNumber); // 현재페이지 번호 넘겨준다
                        // 여기에 체크박스 이벤트 리스너를 다시 설정
                        attachCheckboxEventListeners();
                    }
                })
                .catch(error => {
                    console.error('Error fetching data:', error);
                });
        }

          //가입일 형식 변환
          function formatJoinDate(joinDate) {
              // 입력된 문자열을 Date 객체로 변환
              const date = new Date(joinDate);

              // 원하는 형식으로 날짜와 시간을 추출
              const year = date.getFullYear();
              const month = (date.getMonth() + 1).toString().padStart(2, '0'); // 월은 0부터 시작하므로 1을 더하고 2자리로 패딩
              const day = date.getDate().toString().padStart(2, '0'); // 날짜도 2자리로 패딩

              // YYYY.MM.DD 형식으로 조합
              const formattedDate = `${year}.${month}.${day}`;

              return formattedDate;
          }

        // 검색 결과가 없는 경우 메시지 표시
        function displayNoResultsMessage() {
            let table = document.getElementById('memberList');
            let tbody = table.querySelector('tbody');
           tbody.innerHTML = '<tr><td colspan="9" style="text-align: center;">검색 결과가 없습니다.</td></tr>';
            let paginationContainer = document.querySelector('.pagination ul');
            paginationContainer.innerHTML = '';
        }

        //검색 결과 view에 출력
         function displayResults(members) {
            //테이블 변수 선언
             let table = document.getElementById('memberList');
             let tbody = table.querySelector('tbody');
             // TODO: 테이블의 기존 데이터 삭제
              while (tbody.firstChild) {
                     tbody.removeChild(tbody.firstChild);
              }
                 // TODO: 테이블에 각 회원 정보를 추가하는 로직
                 let index = 1; //넘버링 시작 번호

                 members.forEach(member => {
                   let row = tbody.insertRow(-1); // -1은 마지막 행에 추가
                         let cell1 = row.insertCell(0);
                         cell1.innerHTML = `<input type="checkbox" class="memberCheckbox" id="${member.memberNo}" checked="false">`;
                         let cell2 = row.insertCell(1);
                         cell2.textContent = index++; // 넘버링 출력
                         let cell3 = row.insertCell(2);
                         cell3.textContent = convertRole(member.role); // 회원구분을 변환하여 출력
                         let cell4 = row.insertCell(3);
                         cell4.textContent = member.name;
                         let cell5 = row.insertCell(4);
                         cell5.textContent = member.memberId;
                         let cell6 = row.insertCell(5);
                         cell6.textContent = member.tel;
                         let cell7 = row.insertCell(6);
                         cell7.textContent = member.email;
                         let cell8 = row.insertCell(7);
                         cell8.textContent = member.membershipStatus;
                         let cell9 = row.insertCell(8);
                         cell9.textContent = formatJoinDate(member.joinDate); // 날짜 형식 변환 함수 사용
                 });
         }

         // Role을 한글로 변환하는 함수
         function convertRole(role) {
             switch (role) {
                 case 'EDUCATOR':
                     return '교육자';
                 case 'NORMAL':
                     return '일반';
                 case 'ADMIN':
                     return '운영자';
                 case 'STUDENT':
                     return '학생';
                 default:
                     return role;
             }
         }

// 현재 페이지네이션 범위를 저장할 변수
       let currentStartPage = 1;
// 검색 페이지네이션 생성
        function generatePagination(totalPages,currentPage) {
            const paginationContainer = document.querySelector('.pagination ul');
            paginationContainer.innerHTML = ''; // 기존 페이지네이션 삭제


            // 이전 버튼
            const prevLi = document.createElement('li');
            prevLi.classList.add('page-item');
            const prevButton = document.createElement('a');
            prevButton.classList.add('page-link');
            prevButton.textContent = '이전';
            prevButton.addEventListener('click', () => {
                if (currentStartPage > 1) {
                    currentStartPage -= 4;
                    generatePagination(totalElements);
                    fetchSearchResults();  // 페이지네이션을 변경 후 검색 결과를 다시 가져옴
                }
            });
            prevLi.appendChild(prevButton);
            paginationContainer.appendChild(prevLi);

            // 숫자 버튼
              for (let i = 0; i < 4; i++) {
                  //if (currentStartPage + i > totalPage) break;  // 전체 페이지 수를 초과하면 생성 중단
                   const pageNumber = currentStartPage + i;
                    if (pageNumber > totalPages) break;  // 전체 페이지 수를 초과하면 생성 중단

                  const pageLi = document.createElement('li');
                  pageLi.classList.add('page-item');
                   if (pageNumber === currentPage) {
                       pageLi.classList.add('active');  // 현재 페이지를 나타내는 스타일 추가
                   }
                  const pageButton = document.createElement('a');
                  pageButton.classList.add('page-link');
                  pageButton.textContent = pageNumber;
                 // pageButton.textContent = currentStartPage + i;
                  //숫자 클릭 시 해당 페이지의 데이터 로드
                  pageButton.addEventListener('click', () => {
                     //let params = getSearchParams();
                      //params.page = currentStartPage + i;
                      fetchSearchResults(pageNumber);  // 선택한 페이지에 해당하는 검색 결과를 가져옴
                  });
                      pageLi.appendChild(pageButton);
                      paginationContainer.appendChild(pageLi);
              }

            // 다음 버튼
            const nextLi = document.createElement('li');
            nextLi.classList.add('page-item');
            const nextButton = document.createElement('a');
            nextButton.classList.add('page-link');
            nextButton.textContent = '다음';
            nextButton.addEventListener('click', () => {
                if (currentStartPage + 3 <= totalPage) {
                    currentStartPage += 4;
                    generatePagination(totalElements);
                    fetchSearchResults();  // 페이지네이션을 변경 후 검색 결과를 다시 가져옴
                }
            });
            nextLi.appendChild(nextButton);
            paginationContainer.appendChild(nextLi);
        }

//        //페이지 변경시 검색결과 갱신
//        function changePage(pageNumber) {
//            let params = getSearchParams();
//            params.page = pageNumber;
//            let queryString = new URLSearchParams(params).toString();
//
//            fetch(`/search?${queryString}`)
//            .then(response => response.json())
//            .then(data => {
//                displayResults(data.members);
//            });
//