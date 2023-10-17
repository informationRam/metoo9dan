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
