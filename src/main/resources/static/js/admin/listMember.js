//검색조건 이벤트 , 체크박스 이벤트 처리

//체크박스 선택 이벤트
        document.addEventListener("DOMContentLoaded", function() {
            // 전체 선택 체크박스 클릭 시 모든 체크박스 선택/해제
            const selectAllCheckbox = document.getElementById("selectAllCheckbox");
            const memberCheckboxes = document.querySelectorAll(".memberCheckbox");

            selectAllCheckbox.addEventListener("change", function() {
                memberCheckboxes.forEach(function(checkbox) {
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
        });

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
         function fetchSearchResults() {
             let params = getSearchParams();
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
                        generatePagination(data.totalElements);
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

  // 페이지네이션 생성 함수
  function generatePagination(currentPage, totalPages) {
      let paginationContainer = document.querySelector('.pagination ul');
      paginationContainer.innerHTML = ''; // 기존 페이지네이션 아이템들을 초기화합니다.

      const maxButtons = 3; // 최대 보여질 숫자 버튼 개수
      const halfMaxButtons = Math.floor(maxButtons / 2); // 반 개수

      // 이전 버튼 생성
      const previousButton = document.createElement('li');
      previousButton.classList.add('page-item');
      const previousLink = document.createElement('a');
      previousLink.classList.add('page-link');
      previousLink.href = '#';
      previousLink.textContent = '이전';
      previousButton.appendChild(previousLink);
      paginationContainer.appendChild(previousButton);

      // 숫자 버튼 생성
      for (let i = currentPage - halfMaxButtons; i <= currentPage + halfMaxButtons; i++) {
          if (i >= 1 && i <= totalPages) {
              const pageButton = document.createElement('li');
              pageButton.classList.add('page-item');
              if (i === currentPage) {
                  pageButton.classList.add('active');
              }
              const pageLink = document.createElement('a');
              pageLink.classList.add('page-link');
              pageLink.href = '#';
              pageLink.textContent = i;
              pageButton.appendChild(pageLink);
              paginationContainer.appendChild(pageButton);

              // 페이지 번호를 클릭할 때 데이터를 로드하는 이벤트 핸들러 추가
              pageLink.addEventListener('click', function() {
                  changePage(i);
              });
          }
      }

      // 다음 버튼 생성
      const nextButton = document.createElement('li');
      nextButton.classList.add('page-item');
      const nextLink = document.createElement('a');
      nextLink.classList.add('page-link');
      nextLink.href = '#';
      nextLink.textContent = '다음';
      nextButton.appendChild(nextLink);
      paginationContainer.appendChild(nextButton);

      // 이전 버튼을 클릭할 때 이전 페이지의 데이터 로드
      previousLink.addEventListener('click', function() {
          if (currentPage > 1) {
              changePage(currentPage - 1);
          }
      });

      // 다음 버튼을 클릭할 때 다음 페이지의 데이터 로드
      nextLink.addEventListener('click', function() {
          if (currentPage < totalPages) {
              changePage(currentPage + 1);
          }
      });
  }

  // 페이지 변경시 검색 결과 갱신
  function changePage(pageNumber) {
      let params = getSearchParams();
      params.page = pageNumber;
      let queryString = new URLSearchParams(params).toString();

      fetch(`/admin/search?${queryString}`)
          .then(response => {
              if (!response.ok) {
                  throw new Error('Network response was not ok');
              }
              return response.json();
          })
          .then(data => {
              // 데이터를 표시하는 로직 추가
              displayResults(data.content);
              // 페이지네이션 업데이트
              generatePagination(pageNumber, data.totalPages);
          })
          .catch(error => {
              console.error('Error fetching data:', error);
          });
  }



//        function generatePagination(totalCount) {
//            let totalPages = Math.ceil(totalCount / 15);
//            let paginationContainer = document.querySelector('.pagination ul');
//            paginationContainer.innerHTML = ''; // 기존 페이지네이션 아이템들을 초기화합니다.
//
//            //검색결과 없으면 빈 페이지네이션 표시
//            if (totalPages === 0) {
//                paginationContainer.innerHTML = '검색 결과가 없습니다.';
//            } else {
//                   let previousButton = createPaginationButton('이전', currentPage - 1);
//                   if (currentPage > 1) {
//                       paginationContainer.appendChild(previousButton);
//                   }
//                   for (let i = 1; i <= totalPages; i++) {
//                           let pageListItem = createPaginationButton(i, i);
//                           if (i === currentPage) {
//                               pageListItem.classList.add('active');
//                           }
//                           paginationContainer.appendChild(pageListItem);
//                           }
//                for (let i = 1; i <= totalPages; i++) {
//                           let pageListItem = document.createElement('li');
//                           let pageLink = document.createElement('a');
//                           pageListItem.classList.add('page-item');
//                           pageLink.classList.add('page-link');
//                           pageLink.innerText = i;
//
//                           pageLink.addEventListener('click', function() {
//                               changePage(i);
//                               // 현재 페이지에 "active" 클래스 추가
//                               let activePage = document.querySelector('.pagination .page-item.active');
//                               if (activePage) {
//                                   activePage.classList.remove('active');
//                               }
//                               pageListItem.classList.add('active');
//                           });
//
//                           pageListItem.appendChild(pageLink);
//                           paginationContainer.appendChild(pageListItem);
//                }
//            }
//        }
//
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
//        }




