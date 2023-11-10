
const header = $('#_csrf_header').attr('content');
const token = $('#_csrf').attr('content');

$(document).ajaxSend(function(e,xhr,options) {
   xhr.setRequestHeader(header, token);
});

$(document).ready(function () {

    $('#chatForm').submit(function (e) {
        e.preventDefault();

        const message = $('#message').val();
        $.post('/chat', { message});
        $('#message').val('');
    });

    const eventSource = new EventSource('/chat');
    eventSource.onmessage = function (event) {
        if (event.data) {
            const data = JSON.parse(event.data);
            const dateString = new Date(data.timeStamp).toString();
            const message = `<li class="message"><span class="sender">${data.sender}:</span>${data.message}<small class="timestamp">${dateString}</small></li>`;
            $('#chatMessages').append(message + '');
        }
    };

    eventSource.onerror = function (error) {
        console.error('Chyba SSE:', error);
        eventSource.close();
    };


});
