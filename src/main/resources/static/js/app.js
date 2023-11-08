$(document).ready(function () {

    $('#chatForm').submit(function (e) {
        e.preventDefault();
        const message = $('#message').val();
        $.post('/chat', { message});
        $('#message').val('');
    });

    const eventSource = new EventSource('/chat');
    eventSource.onmessage = function (event) {
        console.log("event is in");
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
