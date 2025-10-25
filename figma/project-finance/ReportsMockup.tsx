import React from 'react';
import { ArrowLeft, Download, Share2, Calendar, Filter, TrendingUp, TrendingDown } from 'lucide-react';

export default function ReportsMockup() {
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
          <span>Báo cáo & Phân tích</span>
          <div className="flex gap-3">
            <Download className="w-5 h-5" />
            <Share2 className="w-5 h-5" />
          </div>
        </div>
        
        {/* Period Selector */}
        <div className="flex gap-2">
          <div className="flex-1 bg-white/20 rounded-lg px-3 py-2 flex items-center gap-2">
            <Calendar className="w-4 h-4" />
            <span className="text-sm">Tháng 10, 2025</span>
          </div>
          <button className="bg-white/20 rounded-lg px-3 py-2">
            <Filter className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto">
        {/* Financial Overview */}
        <div className="p-4">
          <div className="bg-gradient-to-br from-[#2196F3] to-[#1976D2] rounded-xl p-4 text-white shadow-lg mb-4">
            <div className="text-sm opacity-90 mb-1">Tổng quan tài chính</div>
            <div className="grid grid-cols-3 gap-3 mt-3">
              <div>
                <div className="text-xs opacity-80 mb-1">Doanh thu</div>
                <div className="text-xl">$45K</div>
                <div className="text-xs opacity-80 flex items-center gap-1 mt-1">
                  <TrendingUp className="w-3 h-3" />
                  +12%
                </div>
              </div>
              <div>
                <div className="text-xs opacity-80 mb-1">Chi phí</div>
                <div className="text-xl">$28K</div>
                <div className="text-xs opacity-80 flex items-center gap-1 mt-1">
                  <TrendingDown className="w-3 h-3" />
                  -5%
                </div>
              </div>
              <div>
                <div className="text-xs opacity-80 mb-1">Lợi nhuận</div>
                <div className="text-xl">$17K</div>
                <div className="text-xs opacity-80 flex items-center gap-1 mt-1">
                  <TrendingUp className="w-3 h-3" />
                  +24%
                </div>
              </div>
            </div>
          </div>

          {/* Revenue Chart */}
          <div className="bg-white rounded-xl p-4 shadow mb-4">
            <div className="flex items-center justify-between mb-3">
              <span className="text-slate-900">Biểu đồ doanh thu</span>
              <button className="text-[#2196F3] text-xs">6 tháng</button>
            </div>
            
            {/* Simple Bar Chart */}
            <div className="flex items-end justify-between gap-2 h-32 mb-2">
              <div className="flex-1 flex flex-col items-center gap-1">
                <div className="w-full bg-[#2196F3] rounded-t" style={{height: '45%'}} />
                <div className="text-xs text-slate-500">T5</div>
              </div>
              <div className="flex-1 flex flex-col items-center gap-1">
                <div className="w-full bg-[#2196F3] rounded-t" style={{height: '60%'}} />
                <div className="text-xs text-slate-500">T6</div>
              </div>
              <div className="flex-1 flex flex-col items-center gap-1">
                <div className="w-full bg-[#2196F3] rounded-t" style={{height: '75%'}} />
                <div className="text-xs text-slate-500">T7</div>
              </div>
              <div className="flex-1 flex flex-col items-center gap-1">
                <div className="w-full bg-[#2196F3] rounded-t" style={{height: '55%'}} />
                <div className="text-xs text-slate-500">T8</div>
              </div>
              <div className="flex-1 flex flex-col items-center gap-1">
                <div className="w-full bg-[#2196F3] rounded-t" style={{height: '85%'}} />
                <div className="text-xs text-slate-500">T9</div>
              </div>
              <div className="flex-1 flex flex-col items-center gap-1">
                <div className="w-full bg-[#4CAF50] rounded-t" style={{height: '100%'}} />
                <div className="text-xs text-slate-500">T10</div>
              </div>
            </div>

            <div className="text-xs text-slate-500 text-center">
              Tăng trưởng doanh thu 6 tháng
            </div>
          </div>

          {/* Expense Breakdown */}
          <div className="bg-white rounded-xl p-4 shadow mb-4">
            <div className="text-slate-900 mb-3">Phân bổ chi phí</div>
            
            {/* Pie Chart Representation */}
            <div className="flex justify-center mb-4">
              <div className="w-32 h-32 rounded-full relative" style={{
                background: 'conic-gradient(#2196F3 0% 50%, #FF9800 50% 70%, #4CAF50 70% 85%, #9C27B0 85% 100%)'
              }}>
                <div className="absolute inset-4 bg-white rounded-full flex items-center justify-center">
                  <div className="text-center">
                    <div className="text-xl text-slate-900">$28K</div>
                    <div className="text-xs text-slate-500">Tổng</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 bg-[#2196F3] rounded-sm" />
                  <span className="text-slate-600">Nhân sự</span>
                </div>
                <span className="text-slate-900">$14,800 (50%)</span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 bg-[#FF9800] rounded-sm" />
                  <span className="text-slate-600">Marketing</span>
                </div>
                <span className="text-slate-900">$6,000 (20%)</span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 bg-[#4CAF50] rounded-sm" />
                  <span className="text-slate-600">Văn phòng</span>
                </div>
                <span className="text-slate-900">$4,500 (15%)</span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 bg-[#9C27B0] rounded-sm" />
                  <span className="text-slate-600">Khác</span>
                </div>
                <span className="text-slate-900">$2,700 (15%)</span>
              </div>
            </div>
          </div>

          {/* Project Profitability */}
          <div className="bg-white rounded-xl p-4 shadow mb-4">
            <div className="text-slate-900 mb-3">Lợi nhuận theo dự án</div>
            
            <div className="space-y-3">
              <div>
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-slate-700">Website Redesign</span>
                  <span className="text-[#4CAF50]">+35%</span>
                </div>
                <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full bg-[#4CAF50] w-[65%]" />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-slate-700">Mobile App</span>
                  <span className="text-[#FF9800]">+15%</span>
                </div>
                <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full bg-[#FF9800] w-[40%]" />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-slate-700">ERP Integration</span>
                  <span className="text-[#4CAF50]">+42%</span>
                </div>
                <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full bg-[#4CAF50] w-[75%]" />
                </div>
              </div>
            </div>
          </div>

          {/* Customer Analysis */}
          <div className="bg-white rounded-xl p-4 shadow">
            <div className="text-slate-900 mb-3">Top khách hàng</div>
            
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-gradient-to-br from-[#2196F3] to-[#1976D2] rounded-full flex items-center justify-center text-white">
                    A
                  </div>
                  <div>
                    <div className="text-sm text-slate-900">ABC Corporation</div>
                    <div className="text-xs text-slate-500">3 dự án</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-sm text-[#4CAF50]">$35,450</div>
                  <div className="text-xs text-slate-500">Doanh thu</div>
                </div>
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-gradient-to-br from-[#FF9800] to-[#F57C00] rounded-full flex items-center justify-center text-white">
                    X
                  </div>
                  <div>
                    <div className="text-sm text-slate-900">XYZ Ltd.</div>
                    <div className="text-xs text-slate-500">2 dự án</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-sm text-[#4CAF50]">$28,200</div>
                  <div className="text-xs text-slate-500">Doanh thu</div>
                </div>
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-gradient-to-br from-[#4CAF50] to-[#388E3C] rounded-full flex items-center justify-center text-white">
                    T
                  </div>
                  <div>
                    <div className="text-sm text-slate-900">Tech Solutions</div>
                    <div className="text-xs text-slate-500">1 dự án</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-sm text-[#4CAF50]">$18,600</div>
                  <div className="text-xs text-slate-500">Doanh thu</div>
                </div>
              </div>
            </div>
          </div>
        </div>
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
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
          <span className="text-xs">Chi phí</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-[#2196F3]">
          <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
          </svg>
          <span className="text-xs">Báo cáo</span>
        </button>
      </div>
    </div>
  );
}
