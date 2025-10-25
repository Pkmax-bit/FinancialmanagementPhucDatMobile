import React from 'react';
import { Search, Plus, ArrowLeft, Calendar, Tag, Receipt, Camera } from 'lucide-react';

export default function ActualExpenseMockup() {
  const expenses = [
    {
      category: 'Nhân sự',
      icon: '👥',
      description: 'Lương tháng 10/2025',
      amount: 14800,
      date: '01/10/2025',
      project: 'Website Redesign',
      receipt: true,
      approved: true
    },
    {
      category: 'Công cụ',
      icon: '🛠️',
      description: 'Adobe Creative Cloud',
      amount: 79,
      date: '15/10/2025',
      project: 'Brand Identity',
      receipt: true,
      approved: true
    },
    {
      category: 'Văn phòng',
      icon: '🏢',
      description: 'Tiền thuê văn phòng',
      amount: 2500,
      date: '01/10/2025',
      project: 'General',
      receipt: true,
      approved: true
    },
    {
      category: 'Đi lại',
      icon: '✈️',
      description: 'Vé máy bay họp khách hàng',
      amount: 450,
      date: '20/10/2025',
      project: 'Mobile App',
      receipt: false,
      approved: false
    },
  ];

  const totalActual = expenses.reduce((sum, exp) => sum + exp.amount, 0);

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
          <span>Chi phí thực tế</span>
          <Camera className="w-6 h-6" />
        </div>
        
        {/* Search Bar */}
        <div className="bg-white/20 rounded-lg px-4 py-2 flex items-center gap-2">
          <Search className="w-5 h-5" />
          <input
            type="text"
            placeholder="Tìm kiếm chi phí..."
            className="flex-1 bg-transparent outline-none placeholder:text-white/70"
            disabled
          />
        </div>
      </div>

      {/* Summary Card */}
      <div className="bg-white px-4 py-3 border-b border-slate-200">
        <div className="flex items-center justify-between mb-3">
          <div>
            <div className="text-xs text-slate-500 mb-1">Tổng chi tháng 10</div>
            <div className="text-2xl text-[#F44336]">${totalActual.toLocaleString()}</div>
          </div>
          <div className="w-16 h-16 bg-[#F44336]/10 rounded-full flex items-center justify-center">
            <Receipt className="w-8 h-8 text-[#F44336]" />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div className="bg-[#4CAF50]/10 rounded-lg p-2 text-center">
            <div className="text-xs text-slate-500 mb-1">Đã duyệt</div>
            <div className="text-[#4CAF50]">$17,379</div>
          </div>
          <div className="bg-[#FF9800]/10 rounded-lg p-2 text-center">
            <div className="text-xs text-slate-500 mb-1">Chờ duyệt</div>
            <div className="text-[#FF9800]">$450</div>
          </div>
        </div>
      </div>

      {/* Filter Chips */}
      <div className="bg-white px-4 py-3 border-b border-slate-200">
        <div className="flex gap-2 overflow-x-auto">
          <button className="px-3 py-1 bg-[#2196F3] text-white rounded-full text-xs whitespace-nowrap">
            Tất cả (32)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Nhân sự (10)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Văn phòng (8)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Công cụ (7)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Đi lại (5)
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        <div className="space-y-3">
          {expenses.map((expense, index) => (
            <div key={index} className="bg-white rounded-xl p-4 shadow">
              <div className="flex items-start gap-3 mb-3">
                <div className="w-10 h-10 bg-slate-100 rounded-lg flex items-center justify-center text-xl flex-shrink-0">
                  {expense.icon}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between mb-1">
                    <div className="text-slate-900">{expense.description}</div>
                    {expense.receipt ? (
                      <Receipt className="w-4 h-4 text-[#4CAF50] ml-2 flex-shrink-0" />
                    ) : (
                      <Receipt className="w-4 h-4 text-slate-300 ml-2 flex-shrink-0" />
                    )}
                  </div>
                  <div className="flex items-center gap-1 text-xs text-slate-500">
                    <Tag className="w-3 h-3" />
                    <span>{expense.category}</span>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-slate-100">
                <div>
                  <div className="flex items-center gap-1 text-xs text-slate-500 mb-1">
                    <Calendar className="w-3 h-3" />
                    <span>{expense.date}</span>
                  </div>
                  <div className="text-xs text-slate-600">
                    {expense.project}
                  </div>
                </div>
                
                <div className="text-right">
                  <div className="text-[#F44336] mb-1">${expense.amount.toLocaleString()}</div>
                  <div className={`text-xs ${
                    expense.approved ? 'text-[#4CAF50]' : 'text-[#FF9800]'
                  }`}>
                    {expense.approved ? 'Đã duyệt' : 'Chờ duyệt'}
                  </div>
                </div>
              </div>

              {!expense.approved && (
                <div className="mt-3 pt-3 border-t border-slate-100 flex gap-2">
                  <button className="flex-1 bg-[#4CAF50] text-white py-2 rounded-lg text-sm">
                    Duyệt
                  </button>
                  <button className="flex-1 bg-slate-100 text-slate-700 py-2 rounded-lg text-sm">
                    Từ chối
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>

        {/* Comparison Card */}
        <div className="bg-white rounded-xl shadow p-4 mt-4">
          <div className="text-slate-900 mb-3">So sánh kế hoạch vs thực tế</div>
          
          <div className="space-y-3">
            <div>
              <div className="flex justify-between text-sm mb-1">
                <span className="text-slate-600">Kế hoạch tháng 10</span>
                <span className="text-[#2196F3]">$18,500</span>
              </div>
              <div className="flex justify-between text-sm mb-2">
                <span className="text-slate-600">Thực tế</span>
                <span className="text-[#F44336]">$17,829</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div className="h-full bg-[#4CAF50] w-[96%]" />
              </div>
              <div className="text-xs text-[#4CAF50] mt-1">
                Tiết kiệm $671 (3.6%)
              </div>
            </div>

            <div className="pt-3 border-t border-slate-200">
              <div className="text-xs text-slate-500 mb-2">Top danh mục chi tiêu</div>
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-slate-600">👥 Nhân sự</span>
                  <span className="text-slate-900">$14,800 (83%)</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-slate-600">🏢 Văn phòng</span>
                  <span className="text-slate-900">$2,500 (14%)</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-slate-600">✈️ Đi lại</span>
                  <span className="text-slate-900">$450 (2.5%)</span>
                </div>
              </div>
            </div>
          </div>
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
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1.41 16.09V20h-2.67v-1.93c-1.71-.36-3.16-1.46-3.27-3.4h1.96c.1 1.05.82 1.87 2.65 1.87 1.96 0 2.4-.98 2.4-1.59 0-.83-.44-1.61-2.67-2.14-2.48-.6-4.18-1.62-4.18-3.67 0-1.72 1.39-2.84 3.11-3.21V4h2.67v1.95c1.86.45 2.79 1.86 2.85 3.39H14.3c-.05-1.11-.64-1.87-2.22-1.87-1.5 0-2.4.68-2.4 1.64 0 .84.65 1.39 2.67 1.91s4.18 1.39 4.18 3.91c-.01 1.83-1.38 2.83-3.12 3.16z"/>
          </svg>
          <span className="text-xs">Doanh thu</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-[#2196F3]">
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
