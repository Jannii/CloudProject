<%@ Page Title="" Language="C#" MasterPageFile="~/LetsRunMaster.Master" AutoEventWireup="true" CodeBehind="Login.aspx.cs" Inherits="LetsRunWeb.Login" %>
<asp:Content ID="Content1" ContentPlaceHolderID="head" runat="server">
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="ContentPlaceHolder1" runat="server">
    <asp:Label runat="server" Text="Log in" Font-Size="XX-Large"/><br />

    <asp:Label runat="server" Text="Email:" /><asp:TextBox runat="server" ID="emailTB" />
    <!--Validation for Email field-->
    <asp:RequiredFieldValidator runat="server" ControlToValidate="emailTB" ErrorMessage="This field are empty!" />
    <asp:RegularExpressionValidator runat="server" ControlToValidate="emailTB" ErrorMessage ="Not a valid Email!" ValidationExpression="\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*"/>
    <br />
    <asp:Label runat="server" Text="Password:" /><asp:TextBox runat="server" ID="passTB" />
    <asp:RequiredFieldValidator runat="server" ControlToValidate="passTB" ErrorMessage="This field are empty!" />
    <asp:RangeValidator runat="server" ControlToValidate="passTB" ErrorMessage="To short password" MinimumValue="5" MaximumValue="50"/>
    <br />
    <asp:Button runat="server" Text="Log in" OnClick="Login_Click" />
    <br />
    <asp:LinkButton runat="server" Text="Register" OnClick="Reg_Click" CausesValidation="false"/>
</asp:Content>
