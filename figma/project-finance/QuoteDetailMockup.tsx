import React from 'react';
import { ArrowLeft, Share2, Download, Edit } from 'lucide-react';

export default function QuoteDetailMockup() {
  const items = [
    { name: 'UI/UX Design', description: 'Thiết kế giao diện người dùng', quantity: 1, price: 5000, amount: 5000 },
    { name: 'Frontend Development', description: 'Phát triển giao diện', quantity: 1, price: 7000, amount: 7000 },
    { name: 'Backend API', description: 'Phát triển API backend', quantity: 1, price: 3000, amount: 3000 },
  ];

  const subtotal = items.reduce((sum, item) => sum + item.amount, 0);
  const tax = subtotal * 0.1;
  const total = subtotal + tax;

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
          <span>Chi tiết báo giá</span>
          <div className="flex gap-3">
            <Share2 className="w-5 h-5" />
            <Download className="w-5 h-5" />
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto">
        {/* Header Card */}
        <div className="bg-white p-4 border-b border-slate-200">
          <div className="flex items-start justify-between mb-3">
            <div>
              <div className="text-xs text-slate-500 mb-1">Số báo giá</div>
              <div className="text-slate-900">QT-2025-001</div>
            </div>
            <div className="bg-[#2196F3]/10 text-[#2196F3] px-3 py-1 rounded text-xs">
              Đã gửi
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 text-sm">
            <div>
              <div className="text-xs text-slate-500 mb-1">Ngày tạo</div>
              <div className="text-slate-700">25/10/2025</div>
            </div>
            <div>
              <div className="text-xs text-slate-500 mb-1">Hiệu lực đến</div>
              <div className="text-slate-700">15/11/2025</div>
            </div>
          </div>
        </div>

        {/* Customer Info */}
        <div className="bg-white p-4 border-b border-slate-200">
          <div className="text-xs text-slate-500 mb-2">Thông tin khách hàng</div>
          <div className="text-slate-900 mb-1">ABC Corporation</div>
          <div className="text-sm text-slate-600 mb-1">Nguyễn Văn A</div>
          <div className="text-sm text-slate-600 mb-1">nguyenvana@abc.com</div>
          <div className="text-sm text-slate-600">0901234567</div>
        </div>

        {/* Project Info */}
        <div className="bg-white p-4 border-b border-slate-200">
          <div className="text-xs text-slate-500 mb-2">Dự án</div>
          <div className="text-slate-900 mb-1">Website Redesign</div>
          <div className="text-sm text-slate-600">PRJ-2025-001</div>
        </div>

        {/* Items Table */}
        <div className="p-4">
          <div className="bg-white rounded-xl shadow overflow-hidden mb-4">
            <div className="bg-slate-50 px-4 py-3 border-b border-slate-200">
              <span className="text-slate-900">Chi tiết dịch vụ</span>
            </div>

            <div className="divide-y divide-slate-200">
              {items.map((item, index) => (
                <div key={index} className="p-4">
                  <div className="text-slate-900 mb-1">{item.name}</div>
                  <div className="text-xs text-slate-500 mb-2">{item.description}</div>
                  <div className="flex justify-between text-sm">
                    <span className="text-slate-600">
                      {item.quantity} × ${item.price.toLocaleString()}
                    </span>
                    <span className="text-slate-900">${item.amount.toLocaleString()}</span>
                  </div>
                </div>
              ))}
            </div>

            {/* Totals */}
            <div className="border-t-2 border-slate-200 p-4 space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-slate-600">Tạm tính</span>
                <span className="text-slate-900">${subtotal.toLocaleString()}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-slate-600">VAT (10%)</span>
                <span className="text-slate-900">${tax.toLocaleString()}</span>
              </div>
              <div className="flex justify-between pt-2 border-t border-slate-200">
                <span className="text-slate-900">Tổng cộng</span>
                <span className="text-[#2196F3] text-xl">${total.toLocaleString()}</span>
              </div>
            </div>
          </div>

          {/* Terms */}
          <div className="bg-white rounded-xl shadow p-4 mb-4">
            <div className="text-slate-900 mb-2">Điều khoản & Điều kiện</div>
            <div className="text-sm text-slate-600 space-y-1">
              <p>• Thanh toán: 50% trước khi bắt đầu, 50% khi hoàn thành</p>
              <p>• Thời gian thực hiện: 3 tháng</p>
              <p>• Bảo hành: 6 tháng kể từ ngày bàn giao</p>
              <p>• Báo giá có hiệu lực trong 21 ngày</p>
            </div>
          </div>

          {/* Notes */}
          <div className="bg-[#2196F3]/5 rounded-xl p-4">
            <div className="text-slate-900 mb-2 text-sm">Ghi chú</div>
            <div className="text-sm text-slate-600">
              Cảm ơn quý khách đã tin tưởng dịch vụ của chúng tôi. 
              Vui lòng liên hệ nếu có bất kỳ thắc mắc nào.
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="bg-white border-t border-slate-200 p-4 flex gap-3">
        <button className="flex-1 bg-slate-100 text-slate-700 py-3 rounded-lg flex items-center justify-center gap-2">
          <Edit className="w-5 h-5" />
          <span>Chỉnh sửa</span>
        </button>
        <button className="flex-1 bg-[#2196F3] text-white py-3 rounded-lg">
          Gửi cho KH
        </button>
      </div>
    </div>
  );
}
