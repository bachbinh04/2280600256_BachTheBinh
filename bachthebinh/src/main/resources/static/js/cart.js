// Hàm cập nhật giỏ hàng
function updateCart(inputElement) {
    // 1. Lấy ID sách và Số lượng từ thẻ input
    let bookId = inputElement.getAttribute('data-id');
    let quantity = inputElement.value;

    // Kiểm tra số lượng hợp lệ
    if (quantity < 1) {
        alert("Số lượng phải lớn hơn 0");
        inputElement.value = 1;
        quantity = 1;
    }

    // 2. Gọi API cập nhật (AJAX Fetch)
    fetch(`/cart/updateCart/${bookId}/${quantity}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json(); // Chuyển kết quả về JSON
        })
        .then(data => {
            // 3. Cập nhật giao diện với dữ liệu mới nhận được
            
            // Format tiền tệ (VNĐ)
            let itemTotalFormatted = formatCurrency(data.itemTotal);
            let totalPriceFormatted = formatCurrency(data.totalPrice) + ' VNĐ';

            // Cập nhật thành tiền của món hàng đó
            let itemTotalElement = document.getElementById(`item-total-${bookId}`);
            if (itemTotalElement) {
                itemTotalElement.innerText = itemTotalFormatted;
            }

            // Cập nhật tổng tiền đơn hàng
            document.getElementById('order-total').innerText = totalPriceFormatted;
            document.getElementById('final-total').innerText = totalPriceFormatted;
        })
        .catch(error => {
            console.error('Error updating cart:', error);
        });
}

// Hàm format tiền tệ kiểu Việt Nam (VD: 100,000)
function formatCurrency(number) {
    return new Intl.NumberFormat('vi-VN').format(number);
}