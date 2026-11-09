// Base URL para API (permite rodar tanto direto na porta 8080 quanto via Live Server 5500 ou arquivo local)
const API_BASE = window.location.port === '8080' ? '' : 'http://localhost:8080';

// Estado Global do PDV
const state = {
    session: null,
    events: [],
    selectedEvent: null,
    selectedSector: null,
    selectedBatch: null,
    selectedType: 'INTEIRA',
    ticketTypes: [
        { id: 'INTEIRA', name: 'Inteira', mult: 1.0, reqDoc: false },
        { id: 'MEIA_ESTUDANTE', name: 'Meia Estudante', mult: 0.5, reqDoc: true },
        { id: 'MEIA_IDOSO', name: 'Meia Idoso (60+)', mult: 0.5, reqDoc: true },
        { id: 'MEIA_PCD', name: 'Meia PCD', mult: 0.5, reqDoc: true },
        { id: 'MEIA_PROFESSOR', name: 'Meia Professor', mult: 0.5, reqDoc: true },
        { id: 'SOCIAL', name: 'Social (+ 1kg Alim.)', mult: 0.6, reqDoc: false },
        { id: 'CORTESIA', name: 'Cortesia / Staff', mult: 0.0, reqDoc: false }
    ],
    cart: [],
    currentPayMethod: 'CASH'
};

// Inicia aplicação
document.addEventListener('DOMContentLoaded', () => {
    initClock();
    initHotkeys();
    checkCashSession();
    loadEvents();
});

// Relógio em tempo real
function initClock() {
    const el = document.getElementById('clockDisplay');
    setInterval(() => {
        const now = new Date();
        el.textContent = now.toLocaleTimeString('pt-BR');
    }, 1000);
}

// Atalhos de Teclado de PDV
function initHotkeys() {
    window.addEventListener('keydown', (e) => {
        if (e.key === 'F2') {
            e.preventDefault();
            switchTab('venda');
            clearCart();
        } else if (e.key === 'F7') {
            e.preventDefault();
            openCashDrawer();
        } else if (e.key === 'F8') {
            e.preventDefault();
            if (state.cart.length > 0) openPaymentModal();
        } else if (e.key === 'F12') {
            e.preventDefault();
            switchTab('portaria');
        } else if (e.key === 'Escape') {
            closeAllModals();
        }
    });
}

function switchTab(tab) {
    document.querySelectorAll('.hotkey-btn').forEach(b => b.classList.remove('active'));
    document.getElementById('vendaTab').style.display = tab === 'venda' ? 'flex' : 'none';
    document.getElementById('portariaTab').style.display = tab === 'portaria' ? 'flex' : 'none';

    if (tab === 'portaria') {
        setTimeout(() => document.getElementById('checkInInput').focus(), 100);
    }
}

// 1. Verificação & Gestão de Caixa
async function checkCashSession() {
    try {
        const res = await fetch(`${API_BASE}/api/cash-session/current`);
        if (res.status === 200) {
            state.session = await res.json();
            updateSessionUI(true);
            document.getElementById('openCashModal').style.display = 'none';
        } else {
            state.session = null;
            updateSessionUI(false);
            document.getElementById('openCashModal').style.display = 'flex';
        }
    } catch (err) {
        console.error('Erro ao verificar sessão do caixa:', err);
    }
}

function updateSessionUI(isOpen) {
    const badge = document.getElementById('statusBadge');
    const text = document.getElementById('sessionStatusText');
    const terminal = document.getElementById('metaTerminal');
    const op = document.getElementById('metaOperator');
    const fl = document.getElementById('metaFloat');

    if (isOpen && state.session) {
        badge.className = 'status-indicator open';
        text.textContent = 'CAIXA ABERTO';
        terminal.textContent = 'Terminal: ' + state.session.posTerminalId;
        op.textContent = 'Operador: ' + state.session.operatorName;
        fl.textContent = 'Fundo: R$ ' + Number(state.session.initialFloat).toFixed(2).replace('.', ',');
    } else {
        badge.className = 'status-indicator closed';
        text.textContent = 'CAIXA FECHADO';
        terminal.textContent = 'Terminal: --';
        op.textContent = 'Operador: --';
        fl.textContent = 'Fundo: R$ 0,00';
    }
}

async function confirmOpenCash() {
    const body = {
        posTerminalId: document.getElementById('openTerminalId').value,
        operatorId: document.getElementById('openOperatorId').value,
        operatorName: document.getElementById('openOperatorName').value,
        initialFloat: parseFloat(document.getElementById('openInitialFloat').value) || 0
    };

    try {
        const res = await fetch(`${API_BASE}/api/cash-session/open`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (res.ok) {
            state.session = await res.json();
            updateSessionUI(true);
            document.getElementById('openCashModal').style.display = 'none';
            alert('Caixa aberto com sucesso! Bom turno de vendas.');
        } else {
            const err = await res.json();
            if (err.message && err.message.includes('Já existe um caixa aberto')) {
                await checkCashSession();
                document.getElementById('openCashModal').style.display = 'none';
                alert('O caixa já estava aberto para ' + (state.session?.operatorName || 'este operador') + '.\nSessão carregada com sucesso!');
                return;
            }
            alert('Erro: ' + (err.message || 'Falha ao abrir caixa'));
        }
    } catch (e) {
        alert('Erro ao conectar ao servidor.');
    }
}

function closeOpenCashModal() {
    document.getElementById('openCashModal').style.display = 'none';
}

async function openCashDrawer() {
    if (!state.session) {
        await checkCashSession();
    }
    if (!state.session) {
        document.getElementById('openCashModal').style.display = 'flex';
        return;
    }
    document.getElementById('cashDrawerModal').style.display = 'flex';
    loadCashSummary();
}

function closeCashDrawer() {
    document.getElementById('cashDrawerModal').style.display = 'none';
}

async function loadCashSummary() {
    if (!state.session) return;
    try {
        const res = await fetch(`${API_BASE}/api/cash-session/${state.session.sessionId}/summary`);
        if (res.ok) {
            const s = await res.json();
            const container = document.getElementById('cashSummaryCards');
            container.innerHTML = `
                <div class="summary-card"><span>Fundo Inicial:</span><strong>R$ ${fmt(s.initialFloat)}</strong></div>
                <div class="summary-card"><span>Vendas Dinheiro:</span><strong>R$ ${fmt(s.cashSalesTotal)}</strong></div>
                <div class="summary-card"><span>Vendas Cartão Débito:</span><strong>R$ ${fmt(s.debitSalesTotal)}</strong></div>
                <div class="summary-card"><span>Vendas Cartão Crédito:</span><strong>R$ ${fmt(s.creditSalesTotal)}</strong></div>
                <div class="summary-card"><span>Vendas PIX:</span><strong>R$ ${fmt(s.pixSalesTotal)}</strong></div>
                <div class="summary-card"><span>Suprimentos:</span><strong>R$ ${fmt(s.suppliesTotal)}</strong></div>
                <div class="summary-card"><span>Sangrias:</span><strong style="color:var(--danger)">R$ ${fmt(s.bleedTotal)}</strong></div>
                <div class="summary-card" style="border-color:var(--secondary)"><span>Saldo Dinheiro em Gaveta Esperado:</span><strong style="color:var(--secondary)">R$ ${fmt(s.expectedCashBalance)}</strong></div>
                <div class="summary-card" style="border-color:var(--success)"><span>Total Bruto Arrecadado:</span><strong style="color:var(--success)">R$ ${fmt(s.grossTotalSales)}</strong></div>
                <div class="summary-card"><span>Total de Ingressos:</span><strong>${s.ticketsSoldCount} un</strong></div>
            `;
        }
    } catch (e) {
        console.error(e);
    }
}

async function submitMovement() {
    const type = document.getElementById('movType').value;
    const amount = parseFloat(document.getElementById('movAmount').value);
    const reason = document.getElementById('movReason').value;

    if (!amount || amount <= 0) {
        alert('Informe um valor válido');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/cash-session/${state.session.sessionId}/movement`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ type, amount, reason })
        });
        if (res.ok) {
            alert('Movimentação lançada com sucesso!');
            document.getElementById('movAmount').value = '';
            document.getElementById('movReason').value = '';
            loadCashSummary();
        } else {
            const err = await res.json();
            alert('Erro: ' + (err.message || 'Falha ao registrar'));
        }
    } catch (e) {
        alert('Erro ao conectar ao servidor.');
    }
}

async function submitCloseCash() {
    const cashVal = parseFloat(document.getElementById('closeCashCount').value);
    if (isNaN(cashVal)) {
        alert('Por favor informe o valor de dinheiro contado na gaveta.');
        return;
    }

    if (!confirm('Tem certeza que deseja fechar o caixa do turno atual?')) return;

    try {
        const res = await fetch(`${API_BASE}/api/cash-session/${state.session.sessionId}/close`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ closingCashReported: cashVal, closingNotes: 'Fechamento via PDV' })
        });
        if (res.ok) {
            const summary = await res.json();
            alert(`Caixa Fechado com Sucesso!\nSaldo Esperado em Dinheiro: R$ ${fmt(summary.expectedCashBalance)}\nValor Contado: R$ ${fmt(summary.reportedCashBalance)}\nDiferença: R$ ${fmt(summary.cashDifference)}`);
            closeCashDrawer();
            checkCashSession();
        } else {
            const err = await res.json();
            alert('Erro ao fechar caixa: ' + err.message);
        }
    } catch (e) {
        alert('Erro de comunicação.');
    }
}

// 2. Carregar e Renderizar Eventos
async function loadEvents() {
    try {
        const res = await fetch(`${API_BASE}/api/events`);
        if (res.ok) {
            state.events = await res.json();
            renderEvents();
        }
    } catch (e) {
        console.error('Falha ao carregar eventos:', e);
    }
}

function renderEvents() {
    const grid = document.getElementById('eventsGrid');
    grid.innerHTML = '';

    state.events.forEach(ev => {
        const card = document.createElement('div');
        card.className = `event-card ${state.selectedEvent?.eventId === ev.eventId ? 'selected' : ''}`;
        const dt = new Date(ev.dateTime);
        const dtStr = dt.toLocaleDateString('pt-BR') + ' às ' + dt.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });

        card.innerHTML = `
            <span class="event-badge">${ev.ageRating}+ ANOS</span>
            <h4>${ev.name}</h4>
            <p>${ev.artist} • ${ev.venueName} (${ev.city}/${ev.state})</p>
            <div class="event-date-row">📅 ${dtStr}</div>
        `;
        card.onclick = () => selectEvent(ev);
        grid.appendChild(card);
    });
}

function selectEvent(ev) {
    state.selectedEvent = ev;
    state.selectedSector = null;
    state.selectedBatch = null;
    renderEvents();

    const panel = document.getElementById('eventDetailsPanel');
    panel.style.display = 'block';
    document.getElementById('selectedEventName').textContent = ev.name + ' - ' + ev.artist;
    document.getElementById('selectedEventMeta').textContent = `${ev.venueName} | ${ev.city}/${ev.state} | Portões: 18h`;

    renderSectors();
    renderTicketTypes();
    updatePricePreview();
}

function renderSectors() {
    const grid = document.getElementById('sectorsGrid');
    grid.innerHTML = '';

    state.selectedEvent.sectors.forEach(sec => {
        // Encontra o lote ativo
        const activeBatch = sec.batches.find(b => b.active && b.availableQuantity > 0) || sec.batches[0];
        const isSoldOut = sec.availableQuantity <= 0 || !activeBatch;

        const card = document.createElement('div');
        card.className = `sector-card ${state.selectedSector?.sectorId === sec.sectorId ? 'selected' : ''} ${isSoldOut ? 'sold-out' : ''}`;

        card.innerHTML = `
            <div class="sector-title">${sec.name}</div>
            <div class="sector-batch">${activeBatch ? activeBatch.batchNumber + 'º Lote (' + activeBatch.availableQuantity + ' un)' : 'Esgotado'}</div>
            <div class="sector-price">${activeBatch ? 'R$ ' + fmt(activeBatch.basePrice) : 'ESGOTADO'}</div>
        `;

        if (!isSoldOut) {
            card.onclick = () => {
                state.selectedSector = sec;
                state.selectedBatch = activeBatch;
                renderSectors();
                updatePricePreview();
            };
        }
        grid.appendChild(card);
    });

    if (!state.selectedSector && state.selectedEvent.sectors.length > 0) {
        const firstAvail = state.selectedEvent.sectors.find(s => s.availableQuantity > 0);
        if (firstAvail) {
            state.selectedSector = firstAvail;
            state.selectedBatch = firstAvail.batches.find(b => b.active && b.availableQuantity > 0);
            renderSectors();
        }
    }
}

function renderTicketTypes() {
    const grid = document.getElementById('ticketTypesGrid');
    grid.innerHTML = '';

    state.ticketTypes.forEach(t => {
        const pill = document.createElement('div');
        pill.className = `type-pill ${state.selectedType === t.id ? 'selected' : ''}`;
        pill.textContent = t.name;
        pill.onclick = () => {
            state.selectedType = t.id;
            renderTicketTypes();
            updatePricePreview();

            // Documento obrigatório para meia
            const docGroup = document.getElementById('docGroup');
            if (t.reqDoc) {
                docGroup.style.display = 'flex';
            } else {
                docGroup.style.display = 'none';
            }
        };
        grid.appendChild(pill);
    });
}

function adjustQty(delta) {
    const input = document.getElementById('ticketQty');
    let v = parseInt(input.value) || 1;
    v = Math.max(1, Math.min(10, v + delta));
    input.value = v;
    updatePricePreview();
}

function updatePricePreview() {
    if (!state.selectedBatch) return;
    const base = state.selectedBatch.basePrice;
    const typeObj = state.ticketTypes.find(t => t.id === state.selectedType);
    const unit = base * typeObj.mult;
    document.getElementById('previewUnitPrice').textContent = 'R$ ' + fmt(unit);
}

// 3. Adicionar ao Carrinho
function addToCart() {
    if (!state.selectedEvent || !state.selectedSector || !state.selectedBatch) {
        alert('Selecione um evento e setor com lote disponível.');
        return;
    }

    const typeObj = state.ticketTypes.find(t => t.id === state.selectedType);
    const qty = parseInt(document.getElementById('ticketQty').value) || 1;
    const holderName = document.getElementById('holderName').value.trim();
    const holderDoc = document.getElementById('holderDoc').value.trim();

    if (typeObj.reqDoc && !holderDoc) {
        alert('O número do documento/comprovante é obrigatório para meia-entrada!');
        document.getElementById('holderDoc').focus();
        return;
    }

    const unitPrice = state.selectedBatch.basePrice * typeObj.mult;
    const subtotal = unitPrice * qty;

    state.cart.push({
        id: Date.now() + Math.random(),
        eventId: state.selectedEvent.eventId,
        eventName: state.selectedEvent.name,
        sectorId: state.selectedSector.sectorId,
        sectorName: state.selectedSector.name,
        batchId: state.selectedBatch.batchId,
        batchNumber: state.selectedBatch.batchNumber,
        ticketType: typeObj.id,
        ticketTypeName: typeObj.name,
        unitPrice: unitPrice,
        quantity: qty,
        subtotal: subtotal,
        holderName: holderName,
        documentNumber: holderDoc
    });

    renderCart();
    // Limpa campos opcionais
    document.getElementById('holderName').value = '';
    document.getElementById('holderDoc').value = '';
    document.getElementById('ticketQty').value = 1;
}

function renderCart() {
    const list = document.getElementById('cartItemsList');
    if (state.cart.length === 0) {
        list.innerHTML = `
            <div class="cart-empty">
                <span>🛒</span>
                <p>Nenhum ingresso selecionado</p>
                <small>Escolha um show e adicione ingressos</small>
            </div>
        `;
        document.getElementById('btnCheckout').disabled = true;
        document.getElementById('cartSubtotal').textContent = 'R$ 0,00';
        document.getElementById('cartDiscount').textContent = 'R$ 0,00';
        document.getElementById('cartGrandTotal').textContent = 'R$ 0,00';
        return;
    }

    list.innerHTML = '';
    let total = 0;

    state.cart.forEach((item, idx) => {
        total += item.subtotal;
        const div = document.createElement('div');
        div.className = 'cart-item';
        div.innerHTML = `
            <div class="item-info">
                <h5>${item.quantity}x ${item.sectorName} (${item.ticketTypeName})</h5>
                <p>${item.eventName} • Lote ${item.batchNumber}</p>
                ${item.holderName ? '<small>Portador: ' + item.holderName + '</small>' : ''}
            </div>
            <div class="item-total">
                <strong>R$ ${fmt(item.subtotal)}</strong>
                <button class="btn-remove-item" onclick="removeFromCart(${idx})">&times;</button>
            </div>
        `;
        list.appendChild(div);
    });

    document.getElementById('cartSubtotal').textContent = 'R$ ' + fmt(total);
    document.getElementById('cartDiscount').textContent = 'R$ 0,00';
    document.getElementById('cartGrandTotal').textContent = 'R$ ' + fmt(total);
    document.getElementById('btnCheckout').disabled = false;
}

function removeFromCart(idx) {
    state.cart.splice(idx, 1);
    renderCart();
}

function clearCart() {
    state.cart = [];
    renderCart();
}

// 4. Modal de Pagamento
function openPaymentModal() {
    if (state.cart.length === 0) return;
    if (!state.session) {
        alert('O caixa precisa estar aberto para realizar vendas.');
        document.getElementById('openCashModal').style.display = 'flex';
        return;
    }

    const total = state.cart.reduce((sum, item) => sum + item.subtotal, 0);
    document.getElementById('payModalTotal').textContent = 'R$ ' + fmt(total);
    document.getElementById('cashTendered').value = total.toFixed(2);
    document.getElementById('paymentModal').style.display = 'flex';
    selectPayMethod('CASH');
    calculateChange();
}

function closePaymentModal() {
    document.getElementById('paymentModal').style.display = 'none';
}

function selectPayMethod(method) {
    state.currentPayMethod = method;
    document.querySelectorAll('.pay-method-btn').forEach(b => b.classList.remove('active'));
    event?.target?.classList?.add('active');

    document.getElementById('panelCASH').style.display = method === 'CASH' ? 'block' : 'none';
    document.getElementById('panelPIX').style.display = method === 'PIX' ? 'block' : 'none';
    document.getElementById('panelCARD').style.display = (method === 'CREDIT_CARD' || method === 'DEBIT_CARD') ? 'block' : 'none';
}

function calculateChange() {
    const total = state.cart.reduce((sum, item) => sum + item.subtotal, 0);
    const tendered = parseFloat(document.getElementById('cashTendered').value) || 0;
    const change = Math.max(0, tendered - total);
    document.getElementById('cashChangeDisplay').textContent = 'R$ ' + fmt(change);
}

// 5. Finalizar Venda & Gerar Ingressos
async function confirmSale() {
    const total = state.cart.reduce((sum, item) => sum + item.subtotal, 0);
    const tendered = state.currentPayMethod === 'CASH' ? (parseFloat(document.getElementById('cashTendered').value) || total) : total;

    if (state.currentPayMethod === 'CASH' && tendered < total) {
        alert('O valor em dinheiro entregue é menor que o valor total da venda.');
        return;
    }

    const salePayload = {
        sessionId: state.session.sessionId,
        posTerminalId: state.session.posTerminalId,
        operatorName: state.session.operatorName,
        customerCpf: document.getElementById('custCpfInput').value || null,
        discount: 0,
        items: state.cart.map(c => ({
            eventId: c.eventId,
            sectorId: c.sectorId,
            batchId: c.batchId,
            ticketType: c.ticketType,
            quantity: c.quantity,
            holderName: c.holderName || null,
            documentNumber: c.documentNumber || null
        })),
        payments: [
            {
                method: state.currentPayMethod,
                amountTendered: tendered,
                amountApplied: total,
                pixTransactionId: state.currentPayMethod === 'PIX' ? 'PIX_' + Date.now() : null
            }
        ]
    };

    try {
        const res = await fetch(`${API_BASE}/api/sales`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(salePayload)
        });

        if (res.ok) {
            const data = await res.json();
            closePaymentModal();
            clearCart();
            showTicketsPrintPreview(data);
            loadEvents(); // Atualiza contadores de estoque
        } else {
            const err = await res.json();
            alert('Falha na venda: ' + (err.message || 'Erro inesperado'));
        }
    } catch (e) {
        alert('Erro ao enviar pedido para o servidor.');
    }
}

// 6. Impressão Térmica de Ingressos
function showTicketsPrintPreview(saleData) {
    const container = document.getElementById('ticketsPrintContainer');
    container.innerHTML = '';

    saleData.tickets.forEach((t, i) => {
        const ticketDiv = document.createElement('div');
        ticketDiv.className = 'thermal-ticket';
        const dt = new Date(t.eventDate);
        const dtStr = dt.toLocaleDateString('pt-BR') + ' às ' + dt.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });

        ticketDiv.innerHTML = `
            <div class="thermal-header">
                <div class="thermal-title">SHOWTICKET BILHETERIA</div>
                <div class="thermal-venue">${t.venueName}</div>
                <div>Pedido: ${saleData.orderCode} • Ingresso ${i+1}/${saleData.tickets.length}</div>
            </div>
            <div class="thermal-body">
                <div style="font-size:13px; font-weight:bold; text-align:center;">${t.eventName}</div>
                <div style="text-align:center;">📅 ${dtStr}</div>
                <div class="thermal-sector">${t.sectorName}</div>
                <div style="display:flex; justify-content:space-between; margin:4px 0;">
                    <span>Tipo: <strong>${t.ticketType}</strong></span>
                    <span>Valor: <strong>R$ ${fmt(t.pricePaid)}</strong></span>
                </div>
                ${t.holderName ? '<div>Titular: ' + t.holderName + '</div>' : ''}
                ${t.documentNumber ? '<div>Doc: ' + t.documentNumber + '</div>' : ''}
                <div class="thermal-qr">
                    <img src="${t.qrCodeBase64}" alt="QR Code">
                </div>
                <div style="text-align:center; font-size:12px; font-weight:bold; letter-spacing:1px;">
                    ${t.ticketCode}
                </div>
            </div>
            <div class="thermal-footer">
                <div>HASH: ${t.validationHash}</div>
                <div>Terminal: ${saleData.posTerminalId} • Operador: ${saleData.operatorName}</div>
                <div>Apresente este ingresso na portaria do evento</div>
            </div>
        `;
        container.appendChild(ticketDiv);
    });

    document.getElementById('ticketPrintModal').style.display = 'flex';
}

function closePrintModal() {
    document.getElementById('ticketPrintModal').style.display = 'none';
}

function finishAndNewSale() {
    closePrintModal();
    switchTab('venda');
}

// 7. Portaria / Validação de Ingressos
async function submitCheckIn() {
    const input = document.getElementById('checkInInput');
    const val = input.value.trim();
    if (!val) return;

    try {
        const res = await fetch(`${API_BASE}/api/tickets/check-in`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                ticketIdentifier: val,
                gate: 'CATRACA-PORTARIA-01',
                operator: state.session?.operatorName || 'OPERADOR-PORTARIA'
            })
        });

        const data = await res.json();
        const resBox = document.getElementById('checkInResult');
        const badge = document.getElementById('checkInBadge');
        const msg = document.getElementById('checkInMsg');
        const details = document.getElementById('checkInDetails');

        resBox.style.display = 'block';

        if (data.success) {
            playBeep(true);
            resBox.className = 'checkin-result success';
            badge.textContent = 'ACESSO LIBERADO';
            msg.textContent = data.message;
            details.innerHTML = `
                <p><strong>Show:</strong> ${data.eventName}</p>
                <p><strong>Setor:</strong> ${data.sectorName} (${data.ticketType})</p>
                <p><strong>Titular:</strong> ${data.holderName || 'Ao Portador'}</p>
                <p><strong>Código:</strong> ${data.ticketCode}</p>
            `;
        } else {
            playBeep(false);
            resBox.className = 'checkin-result error';
            badge.textContent = 'ENTRADA RECUSADA';
            msg.textContent = data.message;
            details.innerHTML = `<p><strong>Código Consultado:</strong> ${data.ticketCode}</p>`;
        }

        input.value = '';
        input.focus();
    } catch (e) {
        alert('Erro ao consultar portaria.');
    }
}

// Feedback Sonoro usando Web Audio API
function playBeep(success) {
    try {
        const ctx = new (window.AudioContext || window.webkitAudioContext)();
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.connect(gain);
        gain.connect(ctx.destination);

        if (success) {
            osc.frequency.setValueAtTime(880, ctx.currentTime);
            osc.frequency.setValueAtTime(1200, ctx.currentTime + 0.1);
            gain.gain.setValueAtTime(0.2, ctx.currentTime);
            osc.start();
            osc.stop(ctx.currentTime + 0.25);
        } else {
            osc.frequency.setValueAtTime(300, ctx.currentTime);
            gain.gain.setValueAtTime(0.3, ctx.currentTime);
            osc.start();
            osc.stop(ctx.currentTime + 0.4);
        }
    } catch (e) {}
}

function closeAllModals() {
    document.querySelectorAll('.modal-overlay').forEach(m => m.style.display = 'none');
}

function fmt(num) {
    return Number(num || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
