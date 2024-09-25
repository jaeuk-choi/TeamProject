<%@page import="java.util.ArrayList"%>
<%@page import="bean.*"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원 관리</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .container {
            width: 90%;
            margin: 0 auto;
        }
        h1 {
            font-size: 36px;
            margin: 20px 0;
            border-bottom: 2px solid black;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            border: 1px solid black;
            padding: 10px;
            text-align: center;
        }
        th {
            background-color: #f2f2f2;
        }
        .action {
            margin-top: 20px;
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }
        .action button {
            padding: 10px 20px;
            cursor: pointer;
        }
        .pagination {
            text-align: center;
        }
        .pagination button {
            padding: 5px 10px;
            margin: 5px;
        } 
        .excel-download {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 20px;
        }
        .excel-download button {
            padding: 10px 20px;
            cursor: pointer;
        }
        .search-filter {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            margin-bottom: 20px;
        }
        .search-filter select, .search-filter input, .search-filter button {
            margin-right: 10px;
        }
    </style>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
    <script>
        function downloadExcel() {
            var table = document.getElementById("customerTable");
            var wb = XLSX.utils.table_to_book(table, {sheet: "회원 관리"});
            XLSX.writeFile(wb, '회원관리.xlsx');
        }
    </script>
</head>
<body>
    <jsp:useBean id="dao" class="bean.CustomerDAO"/>
    <jsp:useBean id="dto" class="bean.CustomerDTO"/>

    <div class="container">
        <h1>회원 관리</h1>

        <div class="filters">
            <form method="get" action="customer.jsp">
                <div class="date-filter">
                    <input type="date" name="startDate" value="2024-07-01">
                    -
                    <input type="date" name="endDate" value="2024-07-31">
                    <button type="submit">조회</button>
                </div>
            </form>
        </div>

        <form method="get" action="customer.jsp">
            <div class="search-filter">
                <select name="keyField">
                    <option value="cus_name">이름</option>
                    <option value="cus_rank">등급</option>
                </select>
                <input type="text" name="filterValue" id="filterValue">
                <button type="submit">검색</button>
            </div>
        </form>

        <div class="action">
            <button type="button" onclick="location.href='customer_add.jsp'">등록</button>
        </div>

        <table id="customerTable">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>이름</th>
                    <th>성별</th>
                    <th>연락처</th>
                    <th>e-mail</th>
                    <th>회원 등록일</th>
                    <th>회원등급</th>
                    <th>특이사항</th>
                </tr>
            </thead>
            <tbody>
            <% 
            int totalRecord = 0; 	// 총 글의 갯수
    		int numPerPage = 10; 	// 한 페이지당 보여질 글의 갯수
    		int totalPage = 0;  	// 총 페이지 수
    		int nowPage = 0;     	// 현재 페이지, 선택할때마다 값을 바꿔야해서 0
    		int beginPerPage = 0;   // 페이지별 시작번호
    		int pagePerBlock = 6;	// 블럭당 페이지 수
    		int totalBlock = 0;		// 총 블럭 수
    		int nowBlock = 0;		// 현재 블럭
    		
            String keyField = request.getParameter("keyField");
            String filterValue = request.getParameter("filterValue");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");

            if(request.getParameter("nowPage") != null)
    			nowPage = Integer.parseInt(request.getParameter("nowPage"));
    		
    		// 각 페이지의 시작 글 번호를 어떻게 뽑아낼 것인지
    		beginPerPage = nowPage * numPerPage; 
    		// 현재페이지에 페이지당 보여질 글의 갯수를 곱하면 페이지별 시작 번호 알 수 있다.
    		
    		// 토탈블럭은 전체 페이지 개수
    		totalBlock = (int)Math.ceil((double)totalPage / pagePerBlock);
    		
    		if(request.getParameter("nowBlock") != null)
    			nowBlock = Integer.parseInt(request.getParameter("nowBlock"));

            ArrayList<CustomerDTO> list = (ArrayList<CustomerDTO>) dao.getCustomerDTOList(keyField, filterValue);

            if (startDate != null && endDate != null) {
                list = (ArrayList<CustomerDTO>) dao.getCustomerByregdate(startDate, endDate);
            }

            totalRecord = list.size();

            for (int i = beginPerPage; i < beginPerPage + numPerPage && i < totalRecord; i++) {
                CustomerDTO customer = list.get(i);
            %>
                <tr>
                    <td><%= customer.getCus_id() %></td>
                    <td><a href="cusRead.jsp?cus_id=<%= customer.getCus_id() %>"><%= customer.getCus_name() %></a></td>
                    <td><%= customer.getCus_gender() %></td>
                    <td><%= customer.getCus_ph() %></td>
                    <td><%= customer.getCus_mail() %></td>
                    <td><%= customer.getCus_reg() %></td>
                    <td><%= customer.getCus_rank() %></td>
                    <td><%= customer.getCus_note() != null ? customer.getCus_note() : "" %></td> 
                </tr>
            <% 
            }
            %>
            </tbody>
        </table>

        <div class="excel-download">
            <button onclick="downloadExcel()">엑셀 다운</button>
        </div>

      <div class="pagination">
           
        <button>&lt;</button>
        <% if(nowBlock > 0) { %>
			<a class="pagination" href="customer.jsp?nowPage=<%=(nowBlock-1)*pagePerBlock%>&nowBlock=<%=nowBlock-1%>">이전 <%=pagePerBlock %>개</a>
			<% } %>
			&nbsp;&nbsp;&nbsp;
			<%
				for(int i=0; i<pagePerBlock; i++){
			%>
				<a class="pagination" href="customer.jsp?nowPage=<%=(nowBlock * pagePerBlock) + i%>&nowBlock=<%=nowBlock%>">
					<%=(nowBlock * pagePerBlock) + i + 1%></a>
				&nbsp;&nbsp;&nbsp;&nbsp;
			<%
				}
			%>
			&nbsp;&nbsp;&nbsp;
			<% if(totalBlock > nowBlock+1) { %>
			<a class="pagination" href="customer.jsp?nowPage=<%=(nowBlock+1)*pagePerBlock%>&nowBlock=<%=nowBlock+1%>">다음 <%=pagePerBlock %>개</a>
			<% } %>
        <button>&gt;</button>
            
        </div>
    </div>
</body>
</html>
