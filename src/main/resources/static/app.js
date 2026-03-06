document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
    loadBooks();
    loadRentedBooks();

});

// User Management
async function loadUsers() {
    try {
        const response = await fetch('/api/users');
        const users = await response.json();
        const userTableBody = document.querySelector('#userTable tbody');
        userTableBody.innerHTML = '';
        users.forEach(user => {
            const row = document.createElement('tr');
            
            let rowHTML = `
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.role}</td>
            `;

            if (typeof currentUserRole !== 'undefined' && currentUserRole === 'admin') {
                rowHTML += `
                    <td>
                        <select id="role-${user.id}">
                            <option value="normal" ${user.role === 'normal' ? 'selected' : ''}>일반</option>
                            <option value="vip" ${user.role === 'vip' ? 'selected' : ''}>VIP</option>
                        </select>
                        <button onclick="changeUserRole('${user.id}')">변경</button>
                    </td>
                `;
            }

            row.innerHTML = rowHTML;
            userTableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading users:', error);
    }
}

async function changeUserRole(userId) {
    const newRole = document.getElementById(`role-${userId}`).value;
    if (!confirm(`사용자 ${userId}의 등급을 ${newRole}(으)로 변경하시겠습니까?`)) {
        return;
    }

    try {
        const response = await fetch(`/api/users/${userId}/role?newRole=${encodeURIComponent(newRole)}`, {
            method: 'PATCH'
        });
        if (response.ok) {
            alert('등급 변경 성공!');
            loadUsers();
        } else {
            const errorText = await response.text();
            alert('등급 변경 실패: ' + errorText);
        }
    } catch (error) {
        console.error('Error changing user role:', error);
        alert('등급 변경 중 오류가 발생했습니다.');
    }
}

// Book Management
async function loadBooks() {
    try {
        const response = await fetch('/api/books');
        const books = await response.json();
        renderBooks(books);
    } catch (error) {
        console.error('Error loading books:', error);
    }
}

async function searchBooks() {
    const title = document.getElementById('searchTitle').value;
    // 체크박스 상태 확인
    const isFit = document.getElementById('searchExact').checked;

    if (!title) {
        loadBooks();
        return;
    }
    try {
        // isFit 파라미터 추가
        const response = await fetch(`/api/books/title?title=${encodeURIComponent(title)}&isFit=${isFit}`);
        const books = await response.json();
        renderBooks(books);
    } catch (error) {
        console.error('Error searching books:', error);
    }
}

function renderBooks(books) {
    const bookTableBody = document.querySelector('#bookTable tbody');
    bookTableBody.innerHTML = '';
    books.forEach(book => {
        const row = document.createElement('tr');
        const statusClass = book.rented ? 'status-rented' : 'status-available';
        const statusText = book.rented ? '대출 중' : '대출 가능';

        let rowHTML = `
            <td>${book.id}</td>
            <td>${book.category}</td>
            <td>${book.title}</td>
            <td class="${statusClass}">${statusText}</td>
        `;

        if (typeof currentUserRole !== 'undefined' && currentUserRole === 'admin') {
            const returnDate = book.returnDate ? book.returnDate : '-';
            const userId = book.userId ? book.userId : '-';
            rowHTML += `
                <td>${returnDate}</td>
                <td>${userId}</td>
            `;
        }

        if(typeof currentUserRole === 'admin' || currentUserRole === 'vip' || currentUserRole === 'normal'){
            rowHTML += `
                <td>
                    ${!book.rented ?
                    `<button class="action-btn rent-btn" onclick="rentBook('${book.id}')">대출</button>` :
                    `<button class="action-btn return-btn" onclick="returnBook('${book.id}')">반납</button>`
                }
                </td>
        `;
        }
        row.innerHTML = rowHTML;
        bookTableBody.appendChild(row);
    });
}

async function loadRentedBooks() {
    try {
        const response = await fetch(`/api/books/show`);
        const books = await response.json();
        const tableBody = document.querySelector('#rentedBookTable tbody');
        if (tableBody) {
            tableBody.innerHTML = '';
            
            if (books.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="4" style="text-align:center;">대출 중인 도서가 없습니다.</td></tr>';
                return;
            }

            books.forEach(book => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${book.id}</td>
                    <td>${book.title}</td>
                    <td>${book.returnDate}</td>
                    <td>
                        <button class="action-btn return-btn" onclick="returnBook('${book.id}')">반납</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        }
    } catch (error) {
        console.error('Error loading rented books:', error);
    }
}

async function rentBook(bookId) {
    try {
        const response = await fetch(`/api/books/${bookId}/checkout`, {
            method: 'POST'
        });
        if (response.ok) {
            alert('대출 성공!');
            loadBooks();
            loadRentedBooks();
        } else {
            const errorText = await response.text();
            alert('대출 실패: ' + errorText);
        }
    } catch (error) {
        console.error('Error renting book:', error);
        alert('대출 중 오류가 발생했습니다.');
    }
}

async function returnBook(bookId) {
    try {
        const response = await fetch(`/api/books/${bookId}/checkin`, {
            method: 'POST'
        });
        if (response.ok) {
            alert('반납 성공!');
            loadBooks();
            loadRentedBooks();
        } else {
            const errorText = await response.text();
            alert('반납 실패: ' + errorText);
        }
    } catch (error) {
        console.error('Error returning book:', error);
        alert('반납 중 오류가 발생했습니다.');
    }
}
