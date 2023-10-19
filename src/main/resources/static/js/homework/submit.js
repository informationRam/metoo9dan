                function reAction(){
                console.log('폭죽 불붙음');
                 $("#startButton").trigger("click");

                 setTimeout(function(){
                 $("#stopButton").trigger("click");
                 }, 5000);
                }

                let homeworkCurrentPage = 1;
                const homeworkRowsPerPage = 5;

                function homeworkGetTotalRowCount() {
                    return $("#homeworkTable tbody tr:not(.filtered-out)").length;
                }

                function homeworkGetVisibleRowCount() {
                    return $("#homeworkTable tbody tr:visible").length;
                }

                function homeworkUpdatePagination() {
                    let totalRows = homeworkGetTotalRowCount();
                    homeworkCurrentPage = 1;
                    homeworkUpdateTableDisplay();
                }

                function homeworkSearchTable() {
                    let input, filter, table, tr, td, i, j, found;
                    input = document.getElementById("homeworkSearchInput");
                    filter = input.value.toUpperCase();
                    table = document.getElementById("homeworkTable");
                    tr = table.getElementsByTagName("tr");
                    let foundCount = 0;

                    $("#noSearchResult").remove();

                    for (i = 1; i < tr.length; i++) {
                        td = tr[i].getElementsByTagName("td");
                        found = false;
                        for (j = 0; j < td.length; j++) {
                            if (td[j]) {
                                if (td[j].innerHTML.toUpperCase().indexOf(filter) > -1) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found) {
                            $(tr[i]).removeClass("filtered-out");
                            foundCount++;
                        } else {
                            $(tr[i]).addClass("filtered-out");
                        }
                    }

                    if (foundCount == 0) {
                        $("#homeworkTable").hide();
                        $("#homeworkPagination").hide();
                        $("#homeworkTable").after('<p id="noSearchResult">검색 결과가 없습니다</p>');
                    } else {
                        $("#homeworkTable").show();
                        $("#homeworkPagination").show();
                    }

                    homeworkUpdatePagination();
                }

                function homeworkRenderPagination() {
                    let totalRows = homeworkGetTotalRowCount();
                    let totalPages = Math.ceil(totalRows / homeworkRowsPerPage);

                    let startPage = homeworkCurrentPage - 2;
                    let endPage = homeworkCurrentPage + 2;

                    if (startPage < 1) {
                        endPage -= (startPage - 1);
                        startPage = 1;
                    }
                    if (endPage > totalPages) {
                        startPage -= (endPage - totalPages);
                        endPage = totalPages;
                    }
                    startPage = Math.max(1, startPage);

                    let paginationHtml = '';
                    for (let i = startPage; i <= endPage; i++) {


                     paginationHtml += `<li class="page-item ${i === homeworkCurrentPage ? 'active' : ''}"><a class="page-link hw-page-number" data-page="${i}" href="#">${i}</a></li>`;
                    }

                    $("#homeworkPageNumbers").html(paginationHtml);
                    $("#homeworkPrevBtn").prop('disabled', homeworkCurrentPage === 1);
                    $("#homeworkNextBtn").prop('disabled', homeworkCurrentPage === totalPages);
                }

                function homeworkUpdateTableDisplay() {
                    let startRow = (homeworkCurrentPage - 1) * homeworkRowsPerPage;
                    let endRow = startRow + homeworkRowsPerPage - 1;

                    $("#homeworkTable tbody tr").each(function(index, row) {
                        if (index === 0) {
                            $(row).show();
                        } else if ($(row).hasClass("filtered-out")) {
                            $(row).hide();
                        } else if (index > startRow && index <= endRow) {
                            $(row).show();
                        } else {
                            $(row).hide();
                        }
                    });

                    homeworkRenderPagination();
                }

                function homeworkPrevPage() {
                    if(homeworkCurrentPage > 1) {
                        homeworkCurrentPage--;
                        homeworkUpdateTableDisplay();
                    }
                }

                function homeworkNextPage() {
                    let totalPages = Math.ceil(homeworkGetTotalRowCount() / homeworkRowsPerPage);
                    if(homeworkCurrentPage < totalPages) {
                        homeworkCurrentPage++;
                        homeworkUpdateTableDisplay();
                    }
                }

                $(document).ready(function() {
                    homeworkUpdateTableDisplay();

                    $("#homeworkPageNumbers").on("click", ".hw-page-number", function() {
                        homeworkCurrentPage = $(this).data('page');
                        homeworkUpdateTableDisplay();
                    });

                    $("#homeworkPrevBtn").click(function() {
                        if (homeworkCurrentPage > 1) {
                            homeworkCurrentPage--;
                            homeworkUpdateTableDisplay();
                        }
                    });

                    $("#homeworkNextBtn").click(function() {
                        let totalRows = homeworkGetTotalRowCount();
                        if (homeworkCurrentPage < Math.ceil(totalRows / homeworkRowsPerPage)) {
                            homeworkCurrentPage++;
                            homeworkUpdateTableDisplay();
                        }
                    });

                    homeworkUpdateTableDisplay();
                });


              function loadForm(element) {
                  let hwSendNo = element.getAttribute("data-send-no");
                  let isSubmitted = element.classList.contains("table-success");

                  fetch(`/homework/submit/detail/${hwSendNo}`)
                      .then(response => response.json())
                      .then(data => {
                          // 숙제 내용 채우기
                          document.querySelector(".edit-form input[name='homeworkNo']").value = data.homeworkNo;
                          document.querySelector(".edit-form input[name='sendNo']").value = data.sendNo;
                          document.querySelector(".submit-form input[name='homeworkNo']").value = data.homeworkNo;
                          document.querySelector(".submit-form input[name='sendNo']").value = data.sendNo;

                          document.querySelector(".submit-form span.homework-title").textContent = data.homeworkTitle;
                          document.querySelector(".submit-form span.homework-content").textContent = data.homeworkContent;
                          document.querySelector(".submit-form span.member-name").textContent = data.memberName;
                          document.querySelector(".submit-form span.progress").textContent = data.progress;
                          document.querySelector(".submit-form span.due-date").textContent = data.dueDate;
                          document.querySelector(".edit-form span.homework-title").textContent = data.homeworkTitle;
                          document.querySelector(".edit-form span.homework-content").textContent = data.homeworkContent;
                          document.querySelector(".edit-form span.member-name").textContent = data.memberName;
                          document.querySelector(".edit-form span.progress").textContent = data.progress;
                          document.querySelector(".edit-form span.due-date").textContent = data.dueDate;

                          document.querySelector(".edit-form span.currentLevel").textContent = data.progress;
                          document.querySelector(".submit-form span.currentLevel").textContent = data.progress;

                          if (isSubmitted) {
                              // 제출 내용으로 수정 폼 채우기
                              document.querySelector(".edit-form textarea[name='homeworkContent']").value = data.content;
                              document.querySelector(".edit-form textarea[name='additionalQuestion']").value = data.additionalQuestion;
                              document.querySelector(".edit-form input[name='homeworkSubmitNo']").value = data.homeworkSubmitNo;
                              $("#deleteBtn").data('submit-id', data.homeworkSubmitNo);
                              document.querySelector(".submit-form").style.display = 'none';
                            $(".edit-form").addClass('animate__animated animate__zoomIn').show();
                            // 애니메이션 효과 종료 후 클래스 제거
                            $(".edit-form").one('animationend', () => {
                                $(".edit-form").removeClass('animate__animated animate__zoomIn');
                            });
                          } else {
                              // 제출 폼 초기화 및 표시
                              document.querySelector(".submit-form textarea[name='homeworkContent']").value = '';
                              document.querySelector(".submit-form textarea[name='additionalQuestion']").value = '';
                              document.querySelector(".edit-form input[name='homeworkSubmitNo']").value = '';
                              document.querySelector(".edit-form").style.display = 'none';
                            $(".submit-form").addClass('animate__animated animate__zoomIn').show();
                            // 애니메이션 효과 종료 후 클래스 제거
                            $(".submit-form").one('animationend', () => {
                                $(".submit-form").removeClass('animate__animated animate__zoomIn');
                            });
                          }
                      })
                      .catch(error => {
                          console.error("There was a problem with the fetch operation:", error.message);
                      });


              }

              $(document).ready(function() {
                    $("#modalCloseBtn").click(function() {
                        location.reload(); // 페이지 리로드
                    });

                   $('.editIndex').click(function() {
                        $('.edit-form').addClass('animate__animated animate__zoomOut');

                        $(".edit-form").one('animationend', function() {
                            $('.edit-form').hide().removeClass('animate__animated animate__zoomOut');
                        });

                   $('.submitIndex').click(function() {
                        $('.submit-form').addClass('animate__animated animate__zoomOut');

                        $(".submit-form").one('animationend', function() {
                            $('.submit-form').hide().removeClass('animate__animated animate__zoomOut');
                        });
                    });
                });


              // addForm 제출
              $("#addForm").submit(function(e) {
                  e.preventDefault();

                  $(".error-message").text("");  // 초기화

                  $.ajax({
                      url: $(this).attr("action"),
                      type: "POST",
                      data: $(this).serialize(),
                      success: function(response) {
                          reAction(); // 꽃가루 효과
                          $("#successModal").show(); // 모달 보이기
                      },
                      error: function(xhr) {
                          let errors = xhr.responseJSON;

                          // 에러 메시지 출력
                          if (errors.homeworkContent) {
                              $("#homeworkContentError").text(errors.homeworkContent);
                          }
                      }
                  });
              });

              // editForm 제출
              $("#editForm").submit(function(e) {
                  e.preventDefault();

                  $(".error-message").text("");  // 초기화
                  reAction();

                  $.ajax({
                      url: $(this).attr("action"),
                      type: "POST",
                      data: $(this).serialize(),
                      success: function(response) {
                          reAction(); // 꽃가루 효과
                          $("#successModal").show(); // 모달 보이기
                      },
                      error: function(xhr) {
                          let errors = xhr.responseJSON;

                          // 에러 메시지 출력
                          if (errors.homeworkContent) {
                              $("#homeworkEditContentError").text(errors.homeworkContent);
                          }
                      }
                  });
              });

                //삭제 버튼 클릭
                $("#deleteBtn").click(function(){
                    let submitId = $(this).data('submit-id');

                  if(submitId && confirm("제출한 내용을 삭제하시겠습니까?")) {
                    $.ajax({
                        url: `/homework/submit/delete/${submitId}`,
                        type: 'DELETE',
                        success: function(response) {
                            alert("숙제가 삭제되었습니다")
                            location.reload();
                            },
                        error: function(xhr, status, error) {
                            alert('삭제 실패: ' + xhr.responseText);
                            }
                        });
                    } else {
                        alert('숙제 제출 ID가 없습니다. 다시 시도해주세요.');
                    }
                });