import React from 'react';
import { ArrowLeft, Edit, Calendar, DollarSign, TrendingUp, AlertCircle } from 'lucide-react';

export default function ProjectDetailMockup() {
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
        <div className="flex items-center justify-between">
          <ArrowLeft className="w-6 h-6" />
          <span>Chi tiết dự án</span>
          <Edit className="w-6 h-6" />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto">
        {/* Header Card */}
        <div className="bg-white p-4 border-b border-slate-200">
          <div className="flex items-start justify-between mb-3">
            <div>
              <div className="text-slate-900 mb-1">Website Redesign</div>
              <div className="text-xs text-slate-500 mb-2">PRJ-2025-001</div>
              <div className="text-xs text-slate-600">ABC Corporation</div>
            </div>
            <div className="bg-[#4CAF50]/10 text-[#4CAF50] px-2 py-1 rounded text-xs">
              Hoạt động
            </div>
          </div>

          {/* Dates */}
          <div className="flex gap-4 text-xs">
            <div className="flex items-center gap-1 text-slate-600">
              <Calendar className="w-3 h-3" />
              <span>Bắt đầu: 01/10/2025</span>
            </div>
            <div className="flex items-center gap-1 text-slate-600">
              <Calendar className="w-3 h-3" />
              <span>Kết thúc: 31/12/2025</span>
            </div>
          </div>
        </div>

        {/* Statistics Cards */}
        <div className="p-4">
          <div className="grid grid-cols-3 gap-3 mb-4">
            <div className="bg-gradient-to-br from-[#2196F3] to-[#1976D2] rounded-xl p-3 text-white shadow">
              <div className="text-xs opacity-80 mb-1">Ngân sách</div>
              <div className="text-xl mb-1">$15K</div>
              <div className="text-xs opacity-80">100%</div>
            </div>

            <div className="bg-gradient-to-br from-[#FF9800] to-[#F57C00] rounded-xl p-3 text-white shadow">
              <div className="text-xs opacity-80 mb-1">Đã chi</div>
              <div className="text-xl mb-1">$9.8K</div>
              <div className="text-xs opacity-80">65%</div>
            </div>

            <div className="bg-gradient-to-br from-[#4CAF50] to-[#388E3C] rounded-xl p-3 text-white shadow">
              <div className="text-xs opacity-80 mb-1">Còn lại</div>
              <div className="text-xl mb-1">$5.2K</div>
              <div className="text-xs opacity-80">35%</div>
            </div>
          </div>

          {/* Progress Card */}
          <div className="bg-white rounded-xl p-4 shadow mb-4">
            <div className="flex items-center justify-between mb-3">
              <span className="text-slate-900">Tiến độ dự án</span>
              <span className="text-[#2196F3]">65%</span>
            </div>
            <div className="h-3 bg-slate-100 rounded-full overflow-hidden mb-2">
              <div className="h-full bg-gradient-to-r from-[#2196F3] to-[#64B5F6] w-[65%]" />
            </div>
            <div className="flex items-center gap-1 text-xs text-slate-500">
              <TrendingUp className="w-3 h-3" />
              <span>Đúng tiến độ - Ước tính hoàn thành: 28/12/2025</span>
            </div>
          </div>

          {/* Tabs */}
          <div className="bg-white rounded-xl shadow mb-4 overflow-hidden">
            <div className="flex border-b border-slate-200">
              <button className="flex-1 py-3 text-[#2196F3] border-b-2 border-[#2196F3] text-sm">
                Tổng quan
              </button>
              <button className="flex-1 py-3 text-slate-400 text-sm">
                Nhiệm vụ
              </button>
              <button className="flex-1 py-3 text-slate-400 text-sm">
                Chi phí
              </button>
              <button className="flex-1 py-3 text-slate-400 text-sm">
                Báo cáo
              </button>
            </div>

            <div className="p-4">
              {/* Team Members */}
              <div className="mb-4">
                <div className="text-xs text-slate-500 mb-2">Thành viên nhóm</div>
                <div className="flex -space-x-2">
                  <div className="w-8 h-8 bg-[#2196F3] rounded-full border-2 border-white flex items-center justify-center text-white text-xs">
                    A
                  </div>
                  <div className="w-8 h-8 bg-[#4CAF50] rounded-full border-2 border-white flex items-center justify-center text-white text-xs">
                    B
                  </div>
                  <div className="w-8 h-8 bg-[#FF9800] rounded-full border-2 border-white flex items-center justify-center text-white text-xs">
                    C
                  </div>
                  <div className="w-8 h-8 bg-slate-300 rounded-full border-2 border-white flex items-center justify-center text-slate-600 text-xs">
                    +3
                  </div>
                </div>
              </div>

              {/* Description */}
              <div className="mb-4">
                <div className="text-xs text-slate-500 mb-2">Mô tả</div>
                <div className="text-sm text-slate-700">
                  Thiết kế lại giao diện website công ty với phong cách hiện đại, 
                  tối ưu trải nghiệm người dùng và tương thích mobile.
                </div>
              </div>

              {/* Milestones */}
              <div>
                <div className="text-xs text-slate-500 mb-2">Cột mốc quan trọng</div>
                <div className="space-y-2">
                  <div className="flex items-center gap-3">
                    <div className="w-6 h-6 bg-[#4CAF50] rounded-full flex items-center justify-center text-white">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                      </svg>
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-slate-700 line-through">UI/UX Design</div>
                      <div className="text-xs text-slate-500">Hoàn thành 15/10/2025</div>
                    </div>
                  </div>

                  <div className="flex items-center gap-3">
                    <div className="w-6 h-6 bg-[#2196F3] rounded-full flex items-center justify-center text-white">
                      <div className="w-2 h-2 bg-white rounded-full" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-slate-700">Frontend Development</div>
                      <div className="text-xs text-slate-500">Đang thực hiện</div>
                    </div>
                  </div>

                  <div className="flex items-center gap-3">
                    <div className="w-6 h-6 bg-slate-200 rounded-full flex items-center justify-center">
                      <div className="w-2 h-2 bg-slate-400 rounded-full" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-slate-400">Testing & Launch</div>
                      <div className="text-xs text-slate-400">Chưa bắt đầu</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Alerts */}
          <div className="bg-[#FF9800]/10 border-l-4 border-[#FF9800] rounded-lg p-3 flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-[#FF9800] flex-shrink-0 mt-0.5" />
            <div>
              <div className="text-sm text-slate-900 mb-1">Cảnh báo ngân sách</div>
              <div className="text-xs text-slate-600">
                Đã sử dụng 65% ngân sách. Cần theo dõi chi phí chặt chẽ.
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="bg-white border-t border-slate-200 p-4 flex gap-3">
        <button className="flex-1 bg-[#2196F3] text-white py-3 rounded-lg flex items-center justify-center gap-2">
          <DollarSign className="w-5 h-5" />
          <span>Thêm chi phí</span>
        </button>
        <button className="flex-1 bg-slate-100 text-slate-700 py-3 rounded-lg">
          Báo cáo
        </button>
      </div>
    </div>
  );
}
