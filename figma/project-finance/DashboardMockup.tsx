import React from 'react';
import { TrendingUp, TrendingDown, DollarSign, FileText, FolderKanban, Users, Menu, Bell } from 'lucide-react';

export default function DashboardMockup() {
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
          <Menu className="w-6 h-6" />
          <span>Dashboard</span>
          <Bell className="w-6 h-6" />
        </div>
        
        {/* Date Selector */}
        <div className="bg-white/20 rounded-lg px-4 py-2 text-center">
          <span className="text-sm">Tháng 10, 2025</span>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        {/* Summary Cards */}
        <div className="grid grid-cols-2 gap-3 mb-4">
          <div className="bg-white rounded-xl p-4 shadow">
            <div className="flex items-center justify-between mb-2">
              <DollarSign className="w-8 h-8 text-[#4CAF50] bg-[#4CAF50]/10 rounded-lg p-1.5" />
              <TrendingUp className="w-4 h-4 text-[#4CAF50]" />
            </div>
            <div className="text-2xl text-slate-900 mb-1">$45,250</div>
            <div className="text-xs text-slate-500">Doanh thu</div>
          </div>

          <div className="bg-white rounded-xl p-4 shadow">
            <div className="flex items-center justify-between mb-2">
              <DollarSign className="w-8 h-8 text-[#F44336] bg-[#F44336]/10 rounded-lg p-1.5" />
              <TrendingDown className="w-4 h-4 text-[#F44336]" />
            </div>
            <div className="text-2xl text-slate-900 mb-1">$28,430</div>
            <div className="text-xs text-slate-500">Chi phí</div>
          </div>

          <div className="bg-white rounded-xl p-4 shadow">
            <div className="flex items-center justify-between mb-2">
              <FolderKanban className="w-8 h-8 text-[#2196F3] bg-[#2196F3]/10 rounded-lg p-1.5" />
            </div>
            <div className="text-2xl text-slate-900 mb-1">12</div>
            <div className="text-xs text-slate-500">Dự án hoạt động</div>
          </div>

          <div className="bg-white rounded-xl p-4 shadow">
            <div className="flex items-center justify-between mb-2">
              <FileText className="w-8 h-8 text-[#FF9800] bg-[#FF9800]/10 rounded-lg p-1.5" />
            </div>
            <div className="text-2xl text-slate-900 mb-1">8</div>
            <div className="text-xs text-slate-500">Hóa đơn chờ</div>
          </div>
        </div>

        {/* Profit Card */}
        <div className="bg-gradient-to-r from-[#4CAF50] to-[#45a049] rounded-xl p-4 mb-4 text-white shadow">
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm opacity-90">Lợi nhuận tháng này</span>
            <TrendingUp className="w-5 h-5" />
          </div>
          <div className="text-3xl mb-1">$16,820</div>
          <div className="text-xs opacity-80">+24% so với tháng trước</div>
        </div>

        {/* Recent Projects */}
        <div className="mb-4">
          <div className="flex items-center justify-between mb-3">
            <span className="text-slate-900">Dự án gần đây</span>
            <button className="text-[#2196F3] text-sm">Xem tất cả</button>
          </div>

          <div className="space-y-2">
            <div className="bg-white rounded-xl p-4 shadow">
              <div className="flex items-start justify-between mb-2">
                <div>
                  <div className="text-slate-900 mb-1">Website Redesign</div>
                  <div className="text-xs text-slate-500">PRJ-2025-001</div>
                </div>
                <div className="bg-[#4CAF50]/10 text-[#4CAF50] px-2 py-1 rounded text-xs">
                  Hoạt động
                </div>
              </div>
              <div className="mb-2">
                <div className="flex justify-between text-xs text-slate-500 mb-1">
                  <span>Tiến độ</span>
                  <span>65%</span>
                </div>
                <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full bg-[#2196F3] w-[65%]" />
                </div>
              </div>
              <div className="flex justify-between text-xs">
                <span className="text-slate-500">Budget: $15,000</span>
                <span className="text-[#4CAF50]">$9,750 spent</span>
              </div>
            </div>

            <div className="bg-white rounded-xl p-4 shadow">
              <div className="flex items-start justify-between mb-2">
                <div>
                  <div className="text-slate-900 mb-1">Mobile App Development</div>
                  <div className="text-xs text-slate-500">PRJ-2025-002</div>
                </div>
                <div className="bg-[#FF9800]/10 text-[#FF9800] px-2 py-1 rounded text-xs">
                  Chậm tiến độ
                </div>
              </div>
              <div className="mb-2">
                <div className="flex justify-between text-xs text-slate-500 mb-1">
                  <span>Tiến độ</span>
                  <span>40%</span>
                </div>
                <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full bg-[#FF9800] w-[40%]" />
                </div>
              </div>
              <div className="flex justify-between text-xs">
                <span className="text-slate-500">Budget: $25,000</span>
                <span className="text-[#F44336]">$18,680 spent</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Navigation */}
      <div className="bg-white border-t border-slate-200 px-4 py-3 flex justify-around">
        <button className="flex flex-col items-center gap-1 text-[#2196F3]">
          <div className="w-6 h-6 flex items-center justify-center">
            <div className="w-2 h-2 bg-[#2196F3] rounded-full" />
          </div>
          <span className="text-xs">Dashboard</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <FolderKanban className="w-6 h-6" />
          <span className="text-xs">Dự án</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <DollarSign className="w-6 h-6" />
          <span className="text-xs">Doanh thu</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <FileText className="w-6 h-6" />
          <span className="text-xs">Chi phí</span>
        </button>
        <button className="flex flex-col items-center gap-1 text-slate-400">
          <Users className="w-6 h-6" />
          <span className="text-xs">Báo cáo</span>
        </button>
      </div>
    </div>
  );
}
