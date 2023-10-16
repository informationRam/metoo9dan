 // 모달을 생성합니다.
        var myModal = new bootstrap.Modal(document.getElementById('myModal'));

        // 버튼을 가져옵니다.
        var button = document.getElementById('button');

        // 버튼 클릭 이벤트 리스너를 추가합니다.
        button.addEventListener('click', function () {
            // 모달을 표시합니다.
            myModal.show();

            // 모달 내용을 로드하는 함수를 호출합니다.
            loadText();
        });

        function loadText() {
            console.log("2");
            var emailValue = document.getElementById('email').value;
            var xhr = new XMLHttpRequest();
            xhr.open('POST', '/user/searchid', true);

            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

            // email 값을 전송
            var params = 'email=' + emailValue;
            xhr.onload = function () {
                console.log("3: ", this.status);
                if (this.status == 200) {
                    console.log("4");
                    // JSON 응답 파싱
                    var responseJson = JSON.parse(this.responseText);

                    // user.userid 값을 가져와서 모달 내용에 출력
                    var userid = responseJson.userid;
                    console.log("userid:", userid);

                    // 결과를 모달 내용에 출력
                    var modalText = '사용자 ID : ' + userid;

                    // 모달 내용 업데이트
                    var modalBody = document.querySelector('.modal-body');
                    modalBody.innerHTML = modalText;
                }
            };
            xhr.send(params);
        }

document.getElementById('button').addEventListener('click', loadText);
    console.log("1");


button.addEventListener('click', function () {
 console.log("1");
    myModal.show();

    // 모달 내용을 로드하는 함수를 호출합니다.
    loadText();
     console.log("2-1");
});

function loadText() {
    console.log("2");
    var emailValue = document.getElementById('email').value;
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/user/searchid', true);

    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // email 값을 전송
    var params = 'email=' + emailValue;

    xhr.onload = function () {
        console.log("3: ", this.status);
        if (this.status == 200) {
            console.log("4");
            // JSON 응답 파싱
            var responseJson = JSON.parse(this.responseText);

            // user.userid 값을 가져와서 모달 내용에 출력
            var userid = responseJson.userid;
            console.log("userid:", userid);

            // 결과를 모달 내용에 출력
            var modalText = '사용자 ID : ' + userid;
            document.getElementById('text').innerText = modalText;
        }
    };

    xhr.send(params);
}
