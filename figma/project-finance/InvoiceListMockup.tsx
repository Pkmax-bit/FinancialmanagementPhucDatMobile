import React from 'react';
import { Search, Plus, ArrowLeft, Receipt, Calendar } from 'lucide-react';

export default function InvoiceListMockup() {
  const invoices = [
    {
      number: 'INV-2025-001',
      project: 'Website Redesign',
      customer: 'ABC Corporation',
      amount: 7500,
      dueDate: '30/11/2025',
      status: 'Đã thanh toán',
      statusColor: 'green'
    },
    {
      number: 'INV-2025-002',
      project: 'Mobile App Development',
      customer: 'XYZ Ltd.',
      amount: 12500,
      dueDate: '25/11/2025',
      status: 'Đã gửi',
      statusColor: 'blue'
    },
    {
      number: 'INV-2025-003',
      project: 'ERP System Integration',
      customer: 'Tech Solutions',
      amount: 17500,
      dueDate: '20/10/2025',
      status: 'Quá hạn',
      statusColor: 'red'
    },
    {
      number: 'INV-2025-004',
      project: 'Brand Identity Design',
      customer: 'Digital Agency',
      amount: 4000,
      dueDate: '10/12/2025',
      status: 'Nháp',
      statusColor: 'gray'
    },
  ];

  const getStatusColor = (color: string) => {
    const colors = {
      green: { bg: 'bg-[#4CAF50]/10', text: 'text-[#4CAF50]' },
      blue: { bg: 'bg-[#2196F3]/10', text: 'text-[#2196F3]' },
      gray: { bg: 'bg-slate-200', text: 'text-slate-600' },
      red: { bg: 'bg-[#F44336]/10', text: 'text-[#F44336]' },
    };
    return colors[color as keyof typeof colors] || colors.gray;
  };

  return (
    <div className="w-[390px] h-[844px] bg-[#F5F5F5] rounded-3xl shadow-2xl overflow-hidden flex flex-col">
      {/* Status Bar */}
      <div className="h-11 bg-[#2196F3] flex items-center justify-between px-6 text-white text-xs">
        <span>9:41</span>
        <div className="flex gap-1 items-center">
          <div className="w-4 h-3 border border-white rounded-sm" />
          <div className="w-1 h-3 bg-white rounded-sm" />
        </div>
      </div>

      {/* App Bar */}
      <div className="bg-[#2196F3] px-4 py-4 text-white">
        <div className="flex items-center justify-between mb-4">
          <ArrowLeft className="w-6 h-6" />
          <span>Hóa đơn</span>
          <div className="w-6 h-6" />
        </div>
        
        {/* Search Bar */}
        <div className="bg-white/20 rounded-lg px-4 py-2 flex items-center gap-2">
          <Search className="w-5 h-5" />
          <input
            type="text"
            placeholder="Tìm kiếm hóa đơn..."
            className="flex-1 bg-transparent outline-none placeholder:text-white/70"
            disabled
          />
        </div>
      </div>

      {/* Summary Cards */}
      <div className="bg-white px-4 py-3 border-b border-slate-200">
        <div className="grid grid-cols-3 gap-3">
          <div className="text-center">
            <div className="text-2xl text-[#4CAF50] mb-1">$24K</div>
            <div className="text-xs text-slate-500">Đã thu</div>
          </div>
          <div className="text-center border-x border-slate-200">
            <div className="text-2xl text-[#2196F3] mb-1">$12.5K</div>
            <div className="text-xs text-slate-500">Chờ thu</div>
          </div>
          <div className="text-center">
            <div className="text-2xl text-[#F44336] mb-1">$17.5K</div>
            <div className="text-xs text-slate-500">Quá hạn</div>
          </div>
        </div>
      </div>

      {/* Filter Chips */}
      <div className="bg-white px-4 py-3 border-b border-slate-200">
        <div className="flex gap-2 overflow-x-auto">
          <button className="px-3 py-1 bg-[#2196F3] text-white rounded-full text-xs whitespace-nowrap">
            Tất cả (28)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Nháp (4)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Đã gửi (12)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Đã thanh toán (10)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Quá hạn (2)
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        <div className="space-y-3">
          {invoices.map((invoice, index) => {
            const statusColors = getStatusColor(invoice.statusColor);
            
            return (
              <div key={index} className="bg-white rounded-xl p-4 shadow">
                <div className="flex items-start justify-between mb-3">
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 bg-[#2196F3]/10 rounded-lg flex items-center justify-center flex-shrink-0">
                      <Receipt className="w-5 h-5 text-[#2196F3]" />
                    </div>
                    <div>
                      <div className="text-slate-900 mb-1">{invoice.project}</div>
                      <div className="text-xs text-slate-500">{invoice.number}</div>
                    </div>
                  </div>
                  <div className={`px-2 py-1 rounded text-xs ${statusColors.bg} ${statusColors.text}`}>
                    {invoice.status}
                  </div>
                </div>

                <div className="space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="text-xs text-slate-500">Khách hàng</span>
                    <span className="text-sm text-slate-700">{invoice.customer}</span>
                  </div>

                  <div className="flex justify-between items-center">
                    <span className="text-xs text-slate-500">Tổng giá trị</span>
                    <span className="text-slate-900">${invoice.amount.toLocaleString()}</span>
                  </div>

                  <div className="flex justify-between items-center">
                    <span className="text-xs text-slate-500 flex items-center gap-1">
                      <Calendar className="w-3 h-3" />
                      Hạn thanh toán
                    </span>
                    <span className={`text-sm ${
                      invoice.status === 'Quá hạn' ? 'text-[#F44336]' : 'text-slate-700'
                    }`}>
                      {invoice.dueDate}
                    </span>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-2 mt-3 pt-3 border-t border-slate-100">
                  <button className="flex-1 bg-[#2196F3] text-white py-2 rounded-lg text-sm">
                    Xem chi tiết
                  </button>
                  {invoice.status === 'Đã gửi' && (
                    <button className="flex-1 bg-[#4CAF50] text-white py-2 rounded-lg text-sm">
                      Xác nhận thanh toán
                    </button>
                  )}
                  {invoice.status === 'Nháp' && (
                    <button className="flex-1 bg-slate-100 text-slate-700 py-2 rounded-lg text-sm">
                      Gửi
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* FAB */}
      <div className="absolute bottom-20 right-8">
        <button className="w-14 h-14 bg-[#2196F3] rounded-full shadow-lg flex items-center justify-center text-white">
          <Plus className="w-6 h-6" />
        </button>
      </div>

      {/* Bottom Navigation */}
      <div className="bg-white border-t border-slate-200 px-4 py-3 flex justify-around">
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <div className="w-6 h-6 flex items-center justify-center">
            <div className="w-2 h-2 bg-slate-400 rounded-full" />
          </div>
          <span className="text-xs">Dashboard</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
            <path d="M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/>
          </svg>
          <span className="text-xs">Dự án</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-[#2196F3]">
          <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1.41 16.09V20h-2.67v-1.93c-1.71-.36-3.16-1.46-3.27-3.4h1.96c.1 1.05.82 1.87 2.65 1.87 1.96 0 2.4-.98 2.4-1.59 0-.83-.44-1.61-2.67-2.14-2.48-.6-4.18-1.62-4.18-3.67 0-1.72 1.39-2.84 3.11-3.21V4h2.67v1.95c1.86.45 2.79 1.86 2.85 3.39H14.3c-.05-1.11-.64-1.87-2.22-1.87-1.5 0-2.4.68-2.4 1.64 0 .84.65 1.39 2.67 1.91s4.18 1.39 4.18 3.91c-.01 1.83-1.38 2.83-3.12 3.16z"/>
          </svg>
          <span className="text-xs">Doanh thu</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
          <span className="text-xs">Chi phí</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
          </svg>
          <span className="text-xs">Báo cáo</span>
        </button>
      </div>
    </div>
  );
}
