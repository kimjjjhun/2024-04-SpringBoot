$(document).ready(function() {
    // 모두 동의 체크박스 이벤트 리스너
    $("#agree_all").on("change", function() {
        $("input[name='agree']").prop("checked", $(this).is(":checked"));
    });

    // 개별 체크박스 이벤트 리스너
    $("input[name='agree']").on("change", function() {
        if (!$(this).is(":checked")) {
            $("#agree_all").prop("checked", false);
        } else {
            if ($("input[name='agree']:checked").length === $("input[name='agree']").length) {
                $("#agree_all").prop("checked", true);
            }
        }
    });

    // 회원가입 버튼 클릭 이벤트 리스너
    $("#registerBtn").on("click", function() {
        if (!$("#check_1").is(":checked") || !$("#check_2").is(":checked") || !$("#check_3").is(":checked")) {
            alert("모든 필수 약관에 동의 하셔야 다음 단계로 진행 가능합니다");
            return false;
        }
        location.href = '/members/new';
    });
});