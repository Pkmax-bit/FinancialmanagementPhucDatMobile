import React from 'react';
import { ArrowLeft, Save, FolderKanban, FileText, DollarSign, Calendar, Users, ChevronRight } from 'lucide-react';

export default function ProjectFormMockup() {
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
          <span>Thêm dự án mới</span>
          <Save className="w-6 h-6" />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-4">
        <div className="bg-white rounded-xl p-4 shadow mb-4">
          <div className="space-y-4">
            {/* Project Name */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Tên dự án *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <FolderKanban className="w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="Nhập tên dự án"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Project Code */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Mã dự án</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <FileText className="w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="PRJ-2025-XXX (tự động)"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Customer Selection */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Khách hàng *</label>
              <button className="w-full bg-slate-50 rounded-lg px-4 py-3 flex items-center justify-between border border-slate-200">
                <div className="flex items-center gap-3">
                  <Users className="w-5 h-5 text-slate-400" />
                  <span className="text-slate-400">Chọn khách hàng</span>
                </div>
                <ChevronRight className="w-5 h-5 text-slate-400" />
              </button>
            </div>

            {/* Budget */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Ngân sách (USD) *</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                <DollarSign className="w-5 h-5 text-slate-400" />
                <input
                  type="number"
                  placeholder="0.00"
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400"
                  disabled
                />
              </div>
            </div>

            {/* Date Range */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs text-slate-500 mb-2 block">Ngày bắt đầu *</label>
                <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                  <Calendar className="w-5 h-5 text-slate-400" />
                  <input
                    type="text"
                    placeholder="DD/MM/YYYY"
                    className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400 text-sm"
                    disabled
                  />
                </div>
              </div>
              <div>
                <label className="text-xs text-slate-500 mb-2 block">Ngày kết thúc *</label>
                <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-center gap-3 border border-slate-200">
                  <Calendar className="w-5 h-5 text-slate-400" />
                  <input
                    type="text"
                    placeholder="DD/MM/YYYY"
                    className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400 text-sm"
                    disabled
                  />
                </div>
              </div>
            </div>

            {/* Description */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Mô tả dự án</label>
              <div className="bg-slate-50 rounded-lg px-4 py-3 flex items-start gap-3 border border-slate-200">
                <FileText className="w-5 h-5 text-slate-400 mt-0.5" />
                <textarea
                  placeholder="Mô tả chi tiết về dự án..."
                  className="flex-1 bg-transparent outline-none text-slate-900 placeholder:text-slate-400 resize-none"
                  rows={3}
                  disabled
                />
              </div>
            </div>

            {/* Team Members */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Thành viên nhóm</label>
              <button className="w-full bg-slate-50 rounded-lg px-4 py-3 flex items-center justify-between border border-slate-200">
                <div className="flex items-center gap-3">
                  <Users className="w-5 h-5 text-slate-400" />
                  <span className="text-slate-400">Thêm thành viên</span>
                </div>
                <ChevronRight className="w-5 h-5 text-slate-400" />
              </button>
            </div>

            {/* Status */}
            <div>
              <label className="text-xs text-slate-500 mb-2 block">Trạng thái dự án</label>
              <div className="grid grid-cols-2 gap-2">
                <button className="py-2 rounded-lg bg-[#4CAF50] text-white text-sm">
                  Hoạt động
                </button>
                <button className="py-2 rounded-lg bg-slate-100 text-slate-600 text-sm">
                  Tạm dừng
                </button>
                <button className="py-2 rounded-lg bg-slate-100 text-slate-600 text-sm">
                  Hoàn thành
                </button>
                <button className="py-2 rounded-lg bg-slate-100 text-slate-600 text-sm">
                  Hủy
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Info Card */}
        <div className="bg-[#2196F3]/10 border-l-4 border-[#2196F3] rounded-lg p-3">
          <div className="text-xs text-slate-700">
            <span className="text-slate-900">Lưu ý:</span> Các trường có dấu (*) là bắt buộc. 
            Mã dự án sẽ được tự động tạo nếu để trống.
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="bg-white border-t border-slate-200 p-4 flex gap-3">
        <button className="flex-1 bg-slate-100 text-slate-700 py-3 rounded-lg">
          Hủy
        </button>
        <button className="flex-1 bg-[#2196F3] text-white py-3 rounded-lg flex items-center justify-center gap-2">
          <Save className="w-5 h-5" />
          <span>Lưu dự án</span>
        </button>
      </div>
    </div>
  );
}
