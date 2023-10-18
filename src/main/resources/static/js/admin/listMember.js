//검색조건 이벤트 , 체크박스 이벤트 처리

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
            event.preventDefault(); // 기본 폼 제출 동작을 중지
            fetchSearchResults(); // 검색 결과를 가져옵니다.
        });

        //검색 조건 객체 설정
        function getSearchParams() {
            return {
                startDate: document.getElementById('startDate').value,
                endDate: document.getElementById('endDate').value,
                memberType: document.getElementById('memberType').value,
                memberQualification: document.getElementById('memberQualification').value,
                searchCriteria: document.getElementById('searchCriteria').value,
                searchKeyword: document.getElementById('searchKeyword').value,
                page: 1, // 기본 페이지는 1로 설정
                limit: 15 // 페이지당 출력될 회원 수
            };
        }
        //AJAX 검색결과 가져오기
        function fetchSearchResults() {
            let params = getSearchParams();
            let queryString = new URLSearchParams(params).toString();

            fetch(`/search?${queryString}`)
            .then(response => response.json())
            .then(data => {
                displayResults(data.members);
                generatePagination(data.totalCount);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
        }
        //검색 결과 view에 출력
         function displayResults(members) {
             let table = document.getElementById('memberList');
             // TODO: 테이블의 기존 데이터 삭제
             members.forEach(member => {
                 // TODO: 테이블에 각 회원 정보를 추가하는 로직
             });
         }

        //페이지네이션 생성
        function generatePagination(totalCount) {
            let totalPages = Math.ceil(totalCount / 15);
            let paginationContainer = document.querySelector('.pagination');
            paginationContainer.innerHTML = ''; // 이전 페이지네이션 삭제

            for(let i=1; i<=totalPages; i++) {
                let pageLink = document.createElement('a');
                pageLink.innerText = i;
                pageLink.addEventListener('click', function() {
                    changePage(i);
                });
                paginationContainer.appendChild(pageLink);
            }
        }

        //페이지 변경시 검색결과 갱신
        function changePage(pageNumber) {
            let params = getSearchParams();
            params.page = pageNumber;
            let queryString = new URLSearchParams(params).toString();

            fetch(`/search?${queryString}`)
            .then(response => response.json())
            .then(data => {
                displayResults(data.members);
            });
        }




