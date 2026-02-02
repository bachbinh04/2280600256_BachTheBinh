$(document).ready(function () {
    // Sự kiện thay đổi số lượng
    $('.quantity').change(function () {
        let quantity = $(this).val();
        let id = $(this).attr('data-id');
        
        // Kiểm tra số lượng hợp lệ
        if (quantity < 1) {
            alert("Số lượng tối thiểu là 1");
            quantity = 1;
            $(this).val(1);
        }

        $.ajax({
            url: '/cart/updateCart/' + id + '/' + quantity,
            type: 'GET',
            dataType: 'json', // Báo cho JS biết server trả về JSON
            success: function (data) {
                // Định dạng tiền tệ (Ví dụ: 1000 -> 1.000 VNĐ)
                // Lưu ý: Dấu chấm/phẩy tùy thuộc vào Locale trình duyệt, bạn có thể chỉnh cứng nếu muốn
                let formatPrice = (num) => num.toLocaleString('en-US').replace(/,/g, '.') + ' VNĐ'; 
                
                // 1. Cập nhật thành tiền của dòng đó
                $('#item-total-' + id).text(formatPrice(data.itemTotal));
                
                // 2. Cập nhật tổng tiền cả giỏ hàng
                $('#order-total').text(formatPrice(data.totalPrice));
            },
            error: function (xhr) {
                console.log('Lỗi cập nhật giỏ hàng');
            }
        });
    });
});