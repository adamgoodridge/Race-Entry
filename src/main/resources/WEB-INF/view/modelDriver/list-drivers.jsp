
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE HTML>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
</head>

<body>
<div id="wrapper">
    <div id="header">
        <h2>Driver Relationship Manager</h2>
    </div>
</div>



<div id="container">
<div id="content">
    <!-- add button for Driver -->
    <input type="button" value="Add Driver" onclick="window.location.href='form'; return false" />
    <!---- END OF search Drivers-->
    <!---- search Drivers-->

    <table>
        <tbody>
    <tr>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Email</th>
        <th>Action</th>
    </tr>
    <c:forEach var="Driver" items="${drivers}">
        <c:url var="updateLink" value="/Driver/showFormForUpdateDriver">
            <c:param name="DriverId" value="${Driver.id}" />
        </c:url>
        <c:url var="deleteLink" value="/Driver/deleteDriver">
            <c:param name="DriverId" value="${Driver.id}" />
        </c:url>
            <tr>
                <td>${Driver.firstName}</td>
                <td>${Driver.lastName}</td>
                <td>${Driver.email}</td>
                <td>
                    <a href="${updateLink}">Update</a> |
                    <a href="${deleteLink}" onclick="if(!(confirm('Are you sure you want to delete ${Driver.firstName} ${Driver.lastName}'))) return false">Delete</a>
                </td>
            </tr>
        </c:forEach>
</table>
    <br>
    <br>
    <h2>Showing employees</h2>
    <input type="button" value="Add Employee" onclick="window.location.href='showFormForAddEmployee'; return false" class="add-button" />
    <br>
    <table>
    <tr>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Email</th>
    </tr>

    <c:forEach var="employee" items="${employees}">
            <tr>
                <td>${employee.firstName}</td>
                <td>${employee.lastName}</td>
                <td>${employee.email}</td>
            </tr>
        </c:forEach>
</table>
    </div>
</div>
</body>

</html>