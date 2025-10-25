import React from 'react';
import { Search, Plus, ArrowLeft, Calendar, Tag, DollarSign } from 'lucide-react';

export default function PlannedExpenseMockup() {
  const expenses = [
    {
      category: 'Nhân sự',
      icon: '👥',
      description: 'Lương tháng 11/2025',
      amount: 15000,
      date: '01/11/2025',
      project: 'Website Redesign',
      status: 'Chờ duyệt',
      statusColor: 'orange'
    },
    {
      category: 'Công cụ',
      icon: '🛠️',
      description: 'Figma Professional',
      amount: 150,
      date: '05/11/2025',
      project: 'Mobile App',
      status: 'Đã duyệt',
      statusColor: 'green'
    },
    {
      category: 'Marketing',
      icon: '📢',
      description: 'Google Ads Campaign',
      amount: 2000,
      date: '10/11/2025',
      project: 'Brand Identity',
      status: 'Chờ duyệt',
      statusColor: 'orange'
    },
    {
      category: 'Đào tạo',
      icon: '📚',
      description: 'React Advanced Course',
      amount: 500,
      date: '15/11/2025',
      project: 'General',
      status: 'Đã duyệt',
      statusColor: 'green'
    },
  ];

  const getStatusColor = (color: string) => {
    const colors = {
      green: { bg: 'bg-[#4CAF50]/10', text: 'text-[#4CAF50]' },
      orange: { bg: 'bg-[#FF9800]/10', text: 'text-[#FF9800]' },
    };
    return colors[color as keyof typeof colors] || colors.orange;
  };

  const totalPlanned = expenses.reduce((sum, exp) => sum + exp.amount, 0);

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
          <span>Chi phí kế hoạch</span>
          <div className="w-6 h-6" />
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
        <div className="flex items-center justify-between">
          <div>
            <div className="text-xs text-slate-500 mb-1">Tổng kế hoạch tháng 11</div>
            <div className="text-2xl text-[#2196F3]">${totalPlanned.toLocaleString()}</div>
          </div>
          <div className="w-16 h-16 bg-[#2196F3]/10 rounded-full flex items-center justify-center">
            <DollarSign className="w-8 h-8 text-[#2196F3]" />
          </div>
        </div>
      </div>

      {/* Filter Chips */}
      <div className="bg-white px-4 py-3 border-b border-slate-200">
        <div className="flex gap-2 overflow-x-auto">
          <button className="px-3 py-1 bg-[#2196F3] text-white rounded-full text-xs whitespace-nowrap">
            Tất cả (24)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Nhân sự (8)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Công cụ (5)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Marketing (6)
          </button>
          <button className="px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs whitespace-nowrap">
            Đào tạo (3)
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        <div className="space-y-3">
          {expenses.map((expense, index) => {
            const statusColors = getStatusColor(expense.statusColor);
            
            return (
              <div key={index} className="bg-white rounded-xl p-4 shadow">
                <div className="flex items-start gap-3 mb-3">
                  <div className="w-10 h-10 bg-slate-100 rounded-lg flex items-center justify-center text-xl flex-shrink-0">
                    {expense.icon}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between mb-1">
                      <div className="text-slate-900">{expense.description}</div>
                      <div className={`px-2 py-1 rounded text-xs ${statusColors.bg} ${statusColors.text} ml-2`}>
                        {expense.status}
                      </div>
                    </div>
                    <div className="flex items-center gap-1 text-xs text-slate-500">
                      <Tag className="w-3 h-3" />
                      <span>{expense.category}</span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-3 border-t border-slate-100">
                  <div className="flex items-center gap-1 text-xs text-slate-500">
                    <Calendar className="w-3 h-3" />
                    <span>{expense.date}</span>
                  </div>
                  
                  <div className="text-right">
                    <div className="text-xs text-slate-500 mb-1">Dự kiến</div>
                    <div className="text-[#2196F3]">${expense.amount.toLocaleString()}</div>
                  </div>
                </div>

                <div className="mt-2 text-xs text-slate-600">
                  Dự án: {expense.project}
                </div>
              </div>
            );
          })}
        </div>

        {/* Category Summary */}
        <div className="bg-white rounded-xl shadow p-4 mt-4">
          <div className="text-slate-900 mb-3">Phân bổ theo danh mục</div>
          <div className="space-y-3">
            <div>
              <div className="flex justify-between text-sm mb-1">
                <span className="text-slate-600">👥 Nhân sự</span>
                <span className="text-slate-900">$15,000 (85%)</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div className="h-full bg-[#2196F3] w-[85%]" />
              </div>
            </div>

            <div>
              <div className="flex justify-between text-sm mb-1">
                <span className="text-slate-600">📢 Marketing</span>
                <span className="text-slate-900">$2,000 (11%)</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div className="h-full bg-[#FF9800] w-[11%]" />
              </div>
            </div>

            <div>
              <div className="flex justify-between text-sm mb-1">
                <span className="text-slate-600">📚 Đào tạo</span>
                <span className="text-slate-900">$500 (3%)</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div className="h-full bg-[#4CAF50] w-[3%]" />
              </div>
            </div>

            <div>
              <div className="flex justify-between text-sm mb-1">
                <span className="text-slate-600">🛠️ Công cụ</span>
                <span className="text-slate-900">$150 (1%)</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div className="h-full bg-[#9C27B0] w-[1%]" />
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
